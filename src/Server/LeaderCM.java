package Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class LeaderCM extends ConsensusModule {

	private Timer heartbeatTimer;
	// Timer id to distinguish two timer, they should have different approach to
	// handle timeout
	private int TIMER_ID = 1;

	@Override
	protected void run() {
		synchronized (cmLock) {
			sendHeartbeats();
			// start heart-beat timer
			heartbeatTimer = scheduleTimer(HEARTBEAT_INTERVAL, TIMER_ID);
		}
	}

	private void sendHeartbeats() {
		System.out.println("Leader " + newServer.serverId + " sending HEARTBEAT");
		// repair other server logs to match leader log
		repairLog();
	}

	private void repairLog() {
		RPCResponse.setTerm(newServer.currentTerm);

		// maintain last matching logs of leader and each server
		int[] latestMatchingIndex = new int[newServer.serverNum];

		// fill initially assuming all server logs are equal
		// length to leader
		Arrays.fill(latestMatchingIndex, newServer.log.getLastIndex());

		int majorityCommitCounter = 0;
		// iterate through servers
		for (int j = 0; j < newServer.serverNum; j++) {
			int response = -1;

			while (response != 0) {
				ArrayList<Entry> entryList = new ArrayList<Entry>();

				for (int i = latestMatchingIndex[j]; i < newServer.log.getLastIndex() + 1; i++) {
					entryList.add(newServer.log.getEntry(i));
				}

				Entry[] entries = new Entry[entryList.size()];
				entries = entryList.toArray(entries);
				remoteAppendEntries(j, newServer.currentTerm, newServer.serverId, latestMatchingIndex[j],
						newServer.log.getEntry(latestMatchingIndex[j]).getTerm(), entries, cmLastCommitId);
				// decrement log index and retry
				latestMatchingIndex[j]--;

				int[] responses = RPCResponse.getAppendEntryResp(newServer.currentTerm);
				response = responses[j];
			}
			if (latestMatchingIndex[j] >= cmLastCommitId) {
				majorityCommitCounter++;
			}
		}

		if (majorityCommitCounter > newServer.serverNum / 2) {
			cmLastCommitId++;
		}
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		synchronized (cmLock) {
			int term = newServer.currentTerm;

			if (leaderTerm > term) {
				heartbeatTimer.cancel();
				newServer.votedFor = -1;
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}

			// get rpc from itself
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
				// reset
				heartbeatTimer = scheduleTimer(HEARTBEAT_INTERVAL, TIMER_ID);
				sendHeartbeats();
			}
		}
	}

}
