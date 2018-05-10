package Server;

import java.rmi.Naming;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ConsensusModule {
	protected static Log log;
	protected static StateMachine stateMachine;
	protected static int cmRmiPort;
	protected static Object cmLock;
	protected static int cmServerId;
	protected static DAServer cmDAServer;
	protected static int cmLastCommitId;
	
	/*
	 * Election timeout bound
	 */
	protected final int ELECTION_TIMEOUT_MIN=150;
	protected final int ELECTION_TIMEOUT_MAX=300;
	/*
	 * Heart-beat interval
	 */
	protected final static int HEARTBEAT_INTERVAL=75;
	
	public static void initCM(int rmiPort, int serverId, DAServer daServer) {
		// TODO Auto-generated constructor stub
		log = new Log();
		stateMachine = new StateMachine();
		cmLock = new Object();
		cmRmiPort = rmiPort;
		cmServerId = serverId;
		cmDAServer = daServer;
		cmLastCommitId = 0;
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

	private final String getRmiUrl(int serverID) {
		return "rmi://localhost:" + cmRmiPort + "/S" + serverID;
	}

	/*
	 * RMI method call
	 */
	protected final void remoteRequestVote(final int serverID, final int candidateTerm, final int candidateID,
			final int lastLogIndex, final int lastLogTerm) {
		new Thread() {
			@Override
			public void run() {
				String url = getRmiUrl(serverID);
				//System.out.println(url);
				try {
					RPC rpc = (RPC) Naming.lookup(url);
					int response = rpc.requestVote(candidateTerm, candidateID, lastLogIndex, lastLogTerm);
					synchronized (cmLock) {
						RPCResponse.setVote(serverID, response, candidateTerm);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}.start();
	}

	protected final void remoteAppendEntries(final int serverID, final int leaderTerm, final int leaderID,
			final int prevLogIndex, final int prevLogTerm, final Entry[] entries, final int leaderCommit) {
		new Thread() {
			@Override
			public void run() {
				String url = getRmiUrl(serverID);
				try {
					RPC rpc = (RPC) Naming.lookup(url);
					/*
					 * Further implementation
					 */
					int response = rpc.appendEntries(leaderTerm, leaderID, prevLogIndex, prevLogTerm, entries, leaderCommit);
					synchronized (cmLock) {
						RPCResponse.setAppendEntryResp(serverID, response, leaderTerm);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
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
	abstract public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit);
	/*
	 * Return current term if not vote for cand
	 * else 0
	 */
	abstract public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm);

}
