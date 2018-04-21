package Server;

public class Entry {
	private int term;
	private int entryID;
	private int clientID;
	private int clientVal;
	public Entry(int entryID, int term, int clientID, int clientVal) {
		this.entryID = entryID;
		this.term = term;
		this.clientID = clientID;
		this.clientVal = clientVal;
	}
	public int getEntry() {
		return this.entryID;
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
}
