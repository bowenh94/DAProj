package Server;

public class ConsensusModule {
	private Log log;
	private StateMachine stateMachine;
	public ConsensusModule() {
		// TODO Auto-generated constructor stub
		this.log = new Log();
		this.stateMachine = new StateMachine();
	}
}
