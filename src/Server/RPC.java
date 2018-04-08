package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RPC extends Remote {
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) throws RemoteException;

	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm)
			throws RemoteException;
	
	public int installSnapshot(int currentTerm) throws RemoteException;
}