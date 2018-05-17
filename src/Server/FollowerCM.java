package Server;

import java.util.Timer;

import Server.newServer.CmMode;

import java.util.Random;

public class FollowerCM extends ConsensusModule {

	private Timer timeoutTimer;
	private int TIMEOUT_TIMER_ID = 1;

	@Override
	public void run() {
		synchronized (cmLock) {
			newServer.mode = CmMode.FOLLOWER;
			System.out.println("Server " + newServer.serverId + " now is in Follower mode with term " + newServer.currentTerm);

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
			System.out.println("Follower " + newServer.serverId + " has term " + newServer.currentTerm + " and receives LOG ENTRY from Leader " + leaderID + " with term " + leaderTerm);
			this.resetTimeoutTimer();
			int term = newServer.currentTerm;
			if (leaderTerm >= term) {
				newServer.setCurrentTerm(leaderTerm);
			} else {
				// condition 1: Reply false if leader term < currentTerm
				return -1;
			}

			/*
			 * condition 2: Reply false if log does not contain an entry at prevLogIndex
			 * whose term matches prevLogTerm
			 */
			
			Entry entryAtIndex = newServer.log.getEntry(prevLogIndex);
			if (entryAtIndex == null || entryAtIndex.getTerm() != prevLogTerm) {
				return -1;
			}

			// TODO: please check condition 3 & 4 CAREFULLY. Fucking tired, need sleep (Owen)
			/*
			 * condition 3: If an existing entry conflicts with a new one (same index but
			 * different terms), delete the existing entry and all that follow it
			 */

			/* condition 4: Append any new entries not already in the log */
			Entry[] newEntries = newServer.stringtoEntries(entries);
			System.out.println("Follower " + newServer.serverId + " inserts LOG ENTRY");
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
				System.out.println("Follower " + newServer.serverId + " receives VOTE REQUEST from Candidate " + candidateID
						+ " and votes for it");
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
				System.out.println("Follower " + newServer.serverId + " has not received HEARTBEAT from Leader and becomes Candidate to start an election");
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
