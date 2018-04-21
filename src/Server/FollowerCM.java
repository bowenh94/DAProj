package Server;

public class FollowerCM extends ConsensusModule{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void handleTimeout(int timerId) {
		// TODO Auto-generated method stub
		
	}
	
}
