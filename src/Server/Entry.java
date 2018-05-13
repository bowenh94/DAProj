package Server;

public class Entry {
	private int term;
	private int clientID;
	private int clientVal;
	public Entry(int term, int clientID, int clientVal) {
		this.term = term;
		this.clientID = clientID;
		this.clientVal = clientVal;
	}
	public int  getTerm() {
		return this.term;
	}
	public int getClientID(){
		return this.clientID;
	}
	public int getCLientVal() {
		return this.clientVal;
	}
	@Override
	public String toString(){
		return this.term+ ","+this.clientID+","+this.clientVal+";";
	}
}
