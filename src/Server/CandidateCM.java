package Server;

import java.util.Random;
import java.util.Timer;

public class CandidateCM extends ConsensusModule {

	private Timer electionTimeoutTimer;
	// Timer id to distinguish two timer, they should have different approach to handle timeout 
	private int TIMER_ID = 2;

	@Override
	protected void run() {
		synchronized (cmLock) {
			// when become candidate, increment self current term by 1
			cmDAServer.setCurrentTerm(cmDAServer.getCurrentTerm() + 1);
			// start new election
			this.startElection();
		}
	}

	private void startElection() {
		// get current term, set it to RPC response and clear vote list
		int term = cmDAServer.getCurrentTerm();
		RPCResponse.setTerm(term);
		RPCResponse.clearVote(term);
		Random random = new Random();

		electionTimeoutTimer = scheduleTimer(
				random.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN,
				TIMER_ID);

		/*
		 * this req voting should be parallized
		 */
		for (int i = 0; i < cmDAServer.getServerNum(); i++) {
			this.remoteRequestVote(i, term, cmDAServer.getServerId(), cmLastCommitId, log.getLastTerm());
		}
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// if leaderTerm less than my current term, refuse and return my current
		// term
		// otherwise accept the call, return 0 and back to follower mode
		synchronized (cmLock) {
			if (leaderTerm >= cmDAServer.getCurrentTerm()) {
				this.electionTimeoutTimer.cancel();
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}
			return cmDAServer.getCurrentTerm();
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		synchronized (cmLock) {
			// vote for itself and refuse any other vote request
			if (candidateID == cmServerId)
				return 0;
			else
				return cmDAServer.getCurrentTerm();
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		synchronized (cmLock) {
			if (timerId == this.TIMER_ID) {
				this.electionTimeoutTimer.cancel();
				int[] vote = RPCResponse.getVoteResp(cmDAServer.getCurrentTerm());
				int count = 0;
				for (int i = 0; i < vote.length; i++) {
					if (vote[i] == 0)
						count++;
				}
				if (count > cmDAServer.getServerNum() / 2) {
					RPCImpl.startMode(new LeaderCM());
				} else {
					cmDAServer.setCurrentTerm(cmDAServer.getCurrentTerm() + 1);
					this.startElection();
				}
			}
		}

	}

}
