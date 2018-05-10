package Server;

import java.util.LinkedList;

public class Log {
	private LinkedList<Entry> linkedList;

	public Log() {
		// TODO Auto-generated constructor stub
		this.linkedList = new LinkedList<>();
	}

	public LinkedList<Entry> get() {
		return this.linkedList;
	}
	
	public void setLog(LinkedList<Entry> linkedList) {
		this.linkedList = linkedList;
	}

	public void append(Entry e) {
		this.linkedList.add(e);
	}

	public int getLastTerm() {
		Entry last = linkedList.getLast();
		return last.getTerm();
	}

	// @return index of last entry in log
	public int getLastIndex() {
		return (linkedList.size() - 1);
	}

	// @return entry at passed-in index, null if none
	public Entry getEntry(int index) {
		if ((index > -1) && (index < linkedList.size())) {
			return linkedList.get(index);
		}

		return null;
	}
}
