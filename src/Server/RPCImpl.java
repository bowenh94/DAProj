package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RPCImpl extends UnicastRemoteObject implements RPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ConsensusModule consensusModule;

	protected RPCImpl() throws RemoteException {
		super();
	}
	
	public static void startMode(ConsensusModule cModule){
		consensusModule = cModule;
		cModule.run();
	}
	
	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) throws RemoteException {
		// TODO Auto-generated method stub
		return consensusModule.appendEntries(leaderTerm, leaderID, prevLogIndex, prevLogTerm, entries, leaderCommit);
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm)
			throws RemoteException {
		// TODO Auto-generated method stub
		return consensusModule.requestVote(candidateTerm, candidateID, lastLogIndex, lastLogTerm);
	}

}
