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
		System.out.println("Leader " + cmDAServer.getServerId() + " sending HEARTBEAT");
		// repair other server logs to match leader log
		repairLog();
	}

	private void repairLog() {
		RPCResponse.setTerm(cmDAServer.getCurrentTerm());

		// maintain last matching logs of leader and each server
		int[] latestMatchingIndex = new int[cmDAServer.getServerNum()];

		// fill initially assuming all server logs are equal
		// length to leader
		Arrays.fill(latestMatchingIndex, log.getLastIndex());

		// iterate through servers
		for (int j = 0; j < cmDAServer.getServerNum(); j++) {
			int response = -1;

			while (response != 0) {
				ArrayList<Entry> entryList = new ArrayList<Entry>();

				for (int i = latestMatchingIndex[j]; i < log.getLastIndex() + 1; i++) {
					entryList.add(log.getEntry(i));
				}

				Entry[] entries = new Entry[entryList.size()];
				entries = entryList.toArray(entries);
				remoteAppendEntries(j, cmDAServer.getCurrentTerm(), cmDAServer.getServerId(), latestMatchingIndex[j],
						log.getEntry(latestMatchingIndex[j]).getTerm(), entries, cmLastCommitId);
				// decrement log index and retry
				latestMatchingIndex[j]--;

				int[] responses = RPCResponse.getAppendEntryResp(cmDAServer.getCurrentTerm());
				response = responses[j];
			}
		}
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		synchronized (cmLock) {
			int term = cmDAServer.getCurrentTerm();

			if (leaderTerm > term) {
				heartbeatTimer.cancel();
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}
			// get rpc from itself
			else if (leaderTerm == term && leaderID == cmDAServer.getServerId()) {
				return 0;
			}

			return term;
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {

			int term = cmDAServer.getCurrentTerm();
			// Revert to follower if candidate has larger term than
			// current leader
			if (candidateTerm > term) {
				heartbeatTimer.cancel();
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}
			return term;
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		// TODO Auto-generated method stub
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
