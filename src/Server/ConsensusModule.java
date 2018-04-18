package Server;

import java.rmi.Naming;
import java.rmi.Remote;

public abstract class ConsensusModule {
	private Log log;
	private StateMachine stateMachine;
	protected static int mRmiPort;
	
	/*
	 * Election timeout bound
	 */
	protected final static int ELECTION_TIMEOUT_MIN=150;
	protected final static int ELECTION_TIMEOUT_MAX=300;
	/*
	 * Heart-beat interval
	 */
	protected final static int HEARTBEAT_INTERVAL=75;
	
	public ConsensusModule() {
		// TODO Auto-generated constructor stub
		this.log = new Log();
		this.stateMachine = new StateMachine();
	}

	public static void startMode(ConsensusModule mode) {
		mode.run();
	}

	private final String getRmiUrl(int serverID) {
		return "rmi://localhost:" + mRmiPort + "/S" + serverID;
	}

	/*
	 * RMI method call
	 */
	protected final void remoteRequestVote(final int serverID, final int candidateTerm, final int candidateID,
			final int lastLogIndex, final int lastLogTerm) {
		new Thread() {
			public void run() {
				String url = getRmiUrl(serverID);
				try {
					RPC rpc = (RPC) Naming.lookup(url);
					/*
					 * Further implementation
					 */
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.start();
	}

	protected final void remoteAppendEntries(final int serverID, final int leaderTerm, final int leaderID,
			final int prevLogIndex, final int prevLogTerm, final Entry[] entries, final int leaderCommit) {
		new Thread() {
			public void run() {
				String url = getRmiUrl(serverID);
				try {
					RPC rpc = (RPC) Naming.lookup(url);
					/*
					 * Further implementation
					 */
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.start();
	}

	
	/*
	 * abstract method for roles to use
	 */
	abstract protected void run();

	abstract public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit);

	abstract public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm);

}
