package Server;

import java.util.Timer;

import Server.newServer.CmMode;

import java.util.Random;

public class FollowerCM extends ConsensusModule {

	private Timer timeoutTimer;
	private int TIMEOUT_TIMER_ID = 1;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			newServer.mode = CmMode.FOLLOWER;
			System.out.println("S" + newServer.serverId + "." + newServer.currentTerm + ": switched to follower mode.");

			// Create timer to detect missing leader
			Random rand = new Random();
			timeoutTimer = scheduleTimer(
					rand.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN,
					this.TIMEOUT_TIMER_ID); // may need to change timer id here
		}
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, String entries,
			int leaderCommit) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			System.out.println("S" + newServer.serverId + "." + newServer.currentTerm + " Leader term is " + leaderTerm
					+ " with Entry of ");

			this.resetTimeoutTimer();
			int term = newServer.currentTerm;
			if (leaderTerm >= term) {
				newServer.setCurrentTerm(leaderTerm);
				// newServer.votedFor = -1;
			} else {
				// condition 1: Reply false if leader term < currentTerm
				return -1;
			}

			/*
			 * condition 2: Reply false if log doesn¡¯t contain an entry at prevLogIndex
			 * whose term matches prevLogTerm
			 */
			Entry entryAtIndex = newServer.log.getEntry(prevLogIndex);
			if (entryAtIndex == null || entryAtIndex.getTerm() != prevLogTerm) {
				return -1;
			}

			// TODO: please check condition 3 & 4 CAREFULLY. Fukcing too tired, need sleep (Owen)
			/*
			 * condition 3: If an existing entry conflicts with a new one (same index but
			 * different terms), delete the existing entry and all that follow it
			 */

			/* condition 4: Append any new entries not already in the log */
			Entry[] newEntries = newServer.stringtoEntries(entries);
			newServer.log.insert(newEntries, prevLogIndex, prevLogTerm);
			
			/*
			 * condition 5: If leaderCommit > commitIndex, set commitIndex =
			 * min(leaderCommit, index of last new entry)
			 */
			if (leaderCommit > ConsensusModule.cmLastCommitId) {
				if (leaderCommit > newServer.log.getLastIndex()) {
					ConsensusModule.cmLastCommitId =  newServer.log.getLastIndex();
				}
				else{
					ConsensusModule.cmLastCommitId = leaderCommit;
				}
			}
			
			return 0;
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		synchronized (cmLock) {
			int term = newServer.currentTerm;
			if (candidateTerm >= term && newServer.votedFor == -1 && lastLogIndex >= cmLastCommitId) {
				System.out.println("Server " + newServer.serverId + "received vote request from server " + candidateID
						+ " and vote");
				// newServer.setCurrentTerm(candidateTerm);
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
