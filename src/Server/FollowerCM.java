package Server;

import java.util.Timer;
import java.util.Random;

public class FollowerCM extends ConsensusModule {

	private Timer timeoutTimer;
	private int TIMEOUT_TIMER_ID = 1;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			System.out.println("S" + newServer.serverId + "." + newServer.currentTerm + ": switched to follower mode.");

			// Create timer to detect missing leader
			Random rand = new Random();
			timeoutTimer = scheduleTimer(
					rand.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN,
					this.TIMEOUT_TIMER_ID); // may need to change timer id here
		}
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			this.resetTimeoutTimer();
			int term = newServer.currentTerm;
			if (leaderTerm >= term) {
				newServer.setCurrentTerm(leaderTerm);
				newServer.votedFor = -1;
			}
			int termAtIndex = newServer.log.getEntry(prevLogIndex).getTerm();
			if (termAtIndex == prevLogTerm) {
				newServer.log.insert(entries, prevLogIndex, prevLogTerm);
				return 0;
			} else {
				return -1;
			}
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			int term = newServer.currentTerm;
			if (candidateTerm >= term && newServer.votedFor == -1 && lastLogIndex >= cmLastCommitId) {
				System.out.println(
						"Server " + newServer.serverId + "received vote request from server " + candidateID + " and vote");
				newServer.setCurrentTerm(candidateTerm);
				newServer.votedFor = candidateID;
				return 0;
			} else {
				/*
				 * different from source (?)
				 */
				return term;
			}
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			if (timerId == this.TIMEOUT_TIMER_ID) {
				timeoutTimer.cancel();
				System.out.println(newServer.serverId + " has not received heartbeat from leader");
				RPCImpl.startMode(new CandidateCM());
			}
		}
	}

	private void resetTimeoutTimer() {
		timeoutTimer.cancel();
		Random random = new Random();
		timeoutTimer = scheduleTimer(
				random.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN,
				this.TIMEOUT_TIMER_ID);
	}

}
