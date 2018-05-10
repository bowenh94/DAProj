package Server;

import java.util.Timer;
import java.util.LinkedList;
import java.util.Random;

public class FollowerCM extends ConsensusModule {

	private Timer timeoutTimer;
	private int TIMEOUT_TIMER_ID = 1;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			System.out.println("S" + cmServerId + "." + cmDAServer.getCurrentTerm() + ": switched to follower mode.");

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
		synchronized(cmLock) {
			this.resetTimeoutTimer();
			int term = cmDAServer.getCurrentTerm();
			if (leaderTerm >= term) {
				cmDAServer.setCurrentTerm(term);
			}
			int termAtIndex = log.getEntry(prevLogIndex).getTerm();
			if(termAtIndex == prevLogTerm) {
				log.insert(entries, prevLogIndex, prevLogTerm);
				return 0;
			}
			else {
				return -1;
			}
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			int term = cmDAServer.getCurrentTerm();
			if (candidateTerm >= term && cmDAServer.getServerId() == 0 && lastLogIndex >= cmLastCommitId) {
				System.out.println(
						"Server " + cmServerId + "received vote request from server " + candidateID + " and vote");
				cmDAServer.setCurrentTerm(candidateTerm);
				return 0;
			} else {
				cmDAServer.setCurrentTerm(candidateTerm);
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
				System.out.println(cmServerId + " has not received heartbeat from leader");
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
