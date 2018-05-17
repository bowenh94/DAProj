package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RPC extends Remote {

	// Invoke by leader, also used as hb
	// hb send empty entries
	// Return 0 if success, for leader to update itself, else return current term;
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, String entries,
			int leaderCommit) throws RemoteException;

	// Invoke by candidate to gather vote
	// Return 0 if success, for leader to update itself, else return current term;
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm)
			throws RemoteException;

	// for basic consensus algorithm only required appendEntries and requestVote
	// public int installSnapshot(int currentTerm) throws RemoteException;
}