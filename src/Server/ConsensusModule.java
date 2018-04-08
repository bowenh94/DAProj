package Server;

import java.rmi.Naming;
import java.rmi.Remote;

public class ConsensusModule {
	private Log log;
	private StateMachine stateMachine;
	private String state = "Follower";
	private static int mRmiPort;
	public ConsensusModule() {
		// TODO Auto-generated constructor stub
		this.log = new Log();
		this.stateMachine = new StateMachine();
	}
	public String getState() {
		return this.state;
	}
	
	private final String getRmiUrl(int serverID) {
		return "rmi://localhost:" + mRmiPort + "/S" + serverID;
	}
	/*
	 * RMI method call 
	 */
	protected final void remoteRequestVote(final int serverID, final int candidateTerm, final int candidateID,
			final int lastLogIndex, final int lastLogTerm){
		new Thread(){
			public void run(){
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
		new Thread(){
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
}
