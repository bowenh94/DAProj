package Server;

import java.util.HashMap;

public class StateMachine {
	private HashMap<Integer, Integer> map;
	public StateMachine() {
		// TODO Auto-generated constructor stub
		this.map = new HashMap<>();
	}
	public void executeLog(Log log) {
		
	}
	public HashMap<Integer, Integer> get() {
		return this.map;
	}
}
