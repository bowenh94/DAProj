package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RPCImpl extends UnicastRemoteObject implements RPC{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ConsensusModule consensusModule;

	protected RPCImpl() throws RemoteException {
		super();
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	@Override
	public int installSnapshot(int currentTerm) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	*/
	
}
