package Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ConsensusModule {
	protected static Object cmLock;

	public static int cmLastCommitId;
	
	/*
	 * Election timeout bound
	 */
	protected final int ELECTION_TIMEOUT_MIN=1500;
	protected final int ELECTION_TIMEOUT_MAX=3000;
	/*
	 * Heart-beat interval
	 */
	protected final static int HEARTBEAT_INTERVAL=750;
	
	public static void initCM(int rmiPort, int serverId) {
		// TODO Auto-generated constructor stub
		cmLock = new Object();
		cmLastCommitId = 2;
	}
	
	protected final Timer scheduleTimer(long millis, final int timerId){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ConsensusModule.this.handleTimeout(timerId);
			}
		};
		timer.schedule(task, millis);
		return timer;
	}

	/*
	 * RMI method call
	 */
	protected final void remoteRequestVote(final int serverID, final int candidateTerm, final int candidateID,
			final int lastLogIndex, final int lastLogTerm) {
		new Thread() {
			@Override
			public void run() {
				String url = "S"+serverID;
				try {
					Registry registry = LocateRegistry.getRegistry(newServer.initRmiPort+serverID);
					RPC rpc = (RPC) registry.lookup(url);
					
					int response = rpc.requestVote(candidateTerm, candidateID, lastLogIndex, lastLogTerm);
					//System.err.println("S"+ newServer.serverId + " Lookup at "+ newServer.initRmiPort+serverID + " for "+url +", Response is "+response);
					synchronized (cmLock) {
						RPCResponse.setVote(serverID, response, candidateTerm);
					}
				} catch (Exception e) {
					//e.printStackTrace();
					System.out.println("FUCKING REMOTE EXCEPTION");
				}
			}
		}.start();
	}

	protected final void remoteAppendEntries(final int serverID, final int leaderTerm, final int leaderID,
			final int prevLogIndex, final int prevLogTerm, final String entries, final int leaderCommit) {		
		new Thread() {
			@Override
			public void run() {
				String url = "S"+serverID;
				//System.out.println(url);
				try {
					Registry registry = LocateRegistry.getRegistry(newServer.initRmiPort+serverID);
					RPC rpc = (RPC) registry.lookup(url);
					
					//System.out.println("FUCKING upper the line ");
					int response = rpc.appendEntries(leaderTerm, leaderID, prevLogIndex, prevLogTerm, entries, leaderCommit);
					//System.out.println("FUCKING behind the line ");
					//System.err.println("S"+ newServer.serverId + " Lookup at "+ newServer.initRmiPort+serverID + " for "+url +", Response is "+response);
					synchronized (cmLock) {
						RPCResponse.setAppendEntryResp(serverID, response, leaderTerm);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//e.printStackTrace();	
					System.out.println("FUCKING REMOTE EXCEPTION");
				}
			}
		}.start();
		
	}

	
	/*
	 * abstract method for roles to use
	 */
	abstract protected void run();
	
	abstract protected void handleTimeout(int timerId);
	
	/*
	 * Return current term if appendEntry fail 
	 * else 0
	 */
	abstract public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, String entries,
			int leaderCommit);
	/*
	 * Return current term if not vote for cand
	 * else 0
	 */
	abstract public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm);

}
