package Server;

public class ConsensusModule {
	private Log log;
	private StateMachine stateMachine;
	private String state = "Follower";
	public ConsensusModule() {
		// TODO Auto-generated constructor stub
		this.log = new Log();
		this.stateMachine = new StateMachine();
	}
	public String getState() {
		return this.state;
	}
}
