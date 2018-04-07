package Server;

public class ConsensusModule {
	private Log log;
	private StateMachine stateMachine;
	private State state;
	
	public enum State {
		INACTIVE,
		Follwer,
		Candidate,
		Leader
	}
	
	public ConsensusModule() {
		// TODO Auto-generated constructor stub
		this.log = new Log();
		this.stateMachine = new StateMachine();
	}
	public State getState() {
		return this.state;
	}
	public int sendHeartBeat() {
		return 1;
	}
}
