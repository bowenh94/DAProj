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
	public int getEntry(Entry e) {
		return entryID;
	}
	public int  getTerm(Entry e) {
		return term;
	}
	public int getClientID(Entry e){
		return clientID;
	}
	public int getCLientVal(Entry e) {
		return clientVal;
	}
}
