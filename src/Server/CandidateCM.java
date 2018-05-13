package Server;

import java.util.Random;
import java.util.Timer;

import Server.newServer.CmMode;

public class CandidateCM extends ConsensusModule {

	private Timer electionTimeoutTimer;
	// Timer id to distinguish two timer, they should have different approach to handle timeout 
	private int TIMER_ID = 2;

	@Override
	protected void run() {
		synchronized (cmLock) {
			newServer.mode = CmMode.CANDIDATE;
			// when become candidate, increment self current term by 1
			newServer.currentTerm += 1;
			// start new election
			System.out.println("S" + newServer.serverId + " start election");
			this.startElection();
		}
	}

	private void startElection() {
		// get current term, set it to RPC response and clear vote list
		int term = newServer.currentTerm;
		RPCResponse.setTerm(term);
		RPCResponse.clearVote(term);
		Random random = new Random();
		
		//System.out.println("S"+newServer.serverId + " Random is "+ (random.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN));

		electionTimeoutTimer = scheduleTimer(
				random.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN,
				TIMER_ID);

		/*
		 * this req voting should be parallized
		 */
		for (int i = 0; i < newServer.serverNum; i++) {
			this.remoteRequestVote(i, term, newServer.serverId, cmLastCommitId, newServer.log.getLastTerm());
		}	
		/*
		System.out.println("!!!!!!!!!!!!!!"+term);
		this.remoteRequestVote(newServer.serverId, term, newServer.serverId, newServer.log.getLastIndex(), newServer.log.getLastTerm());
		*/		
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// if leaderTerm less than my current term, refuse and return my current
		// term
		// otherwise accept the call, return 0 and back to follower mode
		synchronized (cmLock) {
			if (leaderTerm >= newServer.currentTerm) {
				this.electionTimeoutTimer.cancel();
				newServer.votedFor = -1;
				RPCImpl.startMode(new FollowerCM());
				return 0;
			}
			return newServer.currentTerm;
		}
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		synchronized (cmLock) {
			// vote for itself and refuse any other vote request
			if (candidateID == newServer.serverId){
				newServer.votedFor = newServer.serverId;
				return 0;
			}				
			else
				return newServer.currentTerm;
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		synchronized (cmLock) {
			if (timerId == this.TIMER_ID) {
				this.electionTimeoutTimer.cancel();
				int[] vote = RPCResponse.getVoteResp(newServer.currentTerm);
				int count = 0;
				for (int i = 0; i < vote.length; i++) {
					if (vote[i] == 0)
						count++;
				}
				//System.err.println("S"+ newServer.serverId + " get vote "+ count +" at term "+newServer.currentTerm);
				if (count > newServer.serverNum / 2) {
					RPCImpl.startMode(new LeaderCM());
				} else {
					newServer.currentTerm += 1;
					this.startElection();
				}
			}
		}

	}

}
