package Server;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

import Server.newServer.CmMode;

public class LeaderCM extends ConsensusModule {

	private Timer heartbeatTimer;
	// Timer id to distinguish two timer, they should have different approach to
	// handle timeout
	private int TIMER_ID = 1;
	Integer[] appendEntryResponses;
	private int majorityCommitCounter;
	private int[] latestMatchingIndex;
	private int currentLastIndex;

	public LeaderCM() {
		this.majorityCommitCounter = 0;
		this.latestMatchingIndex = new int[newServer.serverNum];
		this.currentLastIndex = newServer.log.getLastIndex();
	}

	@Override
	protected void run() {
		synchronized (cmLock) {
			newServer.mode = CmMode.LEADER;
			Arrays.fill(latestMatchingIndex, currentLastIndex);
			sendHeartbeats();
			// start heart-beat timer
			heartbeatTimer = scheduleTimer(HEARTBEAT_INTERVAL, TIMER_ID);
		}
	}

	private void sendHeartbeats() {
		System.out.println("Leader " + newServer.serverId + " starts to send HEARTBEAT");

		// maintain last matching logs of leader and each server

		// iterate through servers
		for (int j = 0; j < newServer.serverNum; j++) {
			

			// generate an entry list: from lastMatching index to Last index
			ArrayList<Entry> entryList = new ArrayList<Entry>();
			for (int i = latestMatchingIndex[j]; i < currentLastIndex; i++) {
				entryList.add(newServer.log.getEntry(i));
			}
			System.out.println("S" + newServer.serverId + " send hb to S" + j+", with log size of "+entryList.size());
			Entry[] entries = new Entry[entryList.size()];
			entries = entryList.toArray(entries);


			remoteAppendEntries(j, newServer.currentTerm, newServer.serverId, latestMatchingIndex[j],
					newServer.log.getEntry(latestMatchingIndex[j]).getTerm(), entries, cmLastCommitId);
		}
		System.out.println("Leader " + newServer.serverId + " ENDENDEND to send HEARTBEAT");
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		synchronized (cmLock) {
			int term = newServer.currentTerm;

			// Switch to follower mode if another leader has larger term than
			// current leader
			if (leaderTerm > term) {
				heartbeatTimer.cancel();
				newServer.votedFor = -1;
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}

			// get remote call from itself
			if (leaderID == newServer.serverId) {
				System.out.println("Received HEARTBEAT from myself");
				return 0;
			}

			return term;
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		synchronized (cmLock) {
			int term = newServer.currentTerm;
			// Revert to follower if candidate has larger term than
			// current leader
			if (candidateTerm > term) {
				heartbeatTimer.cancel();
				newServer.votedFor = -1;
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}
			return term;
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		synchronized (cmLock) {
			if (timerId == TIMER_ID) {
				heartbeatTimer.cancel();

				// deal with responses, and decide if commit or not
				appendEntryResponses = RPCResponse.getAppendEntryResp(newServer.currentTerm);
				
				System.out.println("append entry responses:++++++++++++++++++++++++++++++++++++++++++++++++++");
				for(int i:appendEntryResponses){
					System.out.print(i+" ------------ ");
				}
					
				System.out.println("current last index:" + currentLastIndex);
				for (int j = 0; j < newServer.serverNum; j++) {
					if (appendEntryResponses[j] == 0) {
						if(currentLastIndex > cmLastCommitId) {
							System.out.println("leader ---------- counter ++");
							majorityCommitCounter++;
						}
						latestMatchingIndex[j] = currentLastIndex;
					} else if (appendEntryResponses[j] == -1){
						if (latestMatchingIndex[j] > 0) {
							latestMatchingIndex[j]--;
						}
					}
				}
				
				System.out.println("majority :" + majorityCommitCounter);
				if (majorityCommitCounter > newServer.serverNum / 2) {
					System.out.println("___________ commit ++ ______________");
					cmLastCommitId++;
				}
				
				majorityCommitCounter = 0;
				RPCResponse.clearAppendResp(newServer.currentTerm);
				
				// reset
				RPCResponse.setTerm(newServer.currentTerm);
				heartbeatTimer = scheduleTimer(HEARTBEAT_INTERVAL, TIMER_ID);
				currentLastIndex = newServer.log.getLastIndex();
				sendHeartbeats();
			}
		}
	}

}
