package Server;

import java.util.LinkedList;

public class Log {
	private LinkedList<Entry> linkedList;

	public Log() {
		// TODO Auto-generated constructor stub
		this.linkedList = new LinkedList<>();
//		this.linkedList.add(new Entry(0, 0, 0));
		/*
		 * for test 
		 */
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
	
	public int append(Entry[] entries) {
		try {
			if(entries != null) {
				for(Entry entry : entries) {
					if(entry != null) {
						linkedList.add(entry);
					}
					else {
						break;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return linkedList.size() - 1;
	}
	
	public int insert(Entry[] entries, int prevIndex, int prevTerm) {
		try {
			if(prevIndex == (linkedList.size()-1)) {
				return append(entries);
			}
			else if(entries == null) {
				return -1;
			}
			else if((prevIndex == -1) ||
					((linkedList.get(prevIndex) != null) &&
							(linkedList.get(prevIndex).getTerm() == prevTerm))) {
				LinkedList<Entry> tmpEntries = new LinkedList<Entry> ();
				for(int i=0; i <= prevIndex; i++) {
					Entry entry=linkedList.get(i);
					tmpEntries.add(entry);
				}
				for(Entry entry:entries) {
					if(entry != null) {
						tmpEntries.add(entry);
					}
				}
				linkedList=tmpEntries;
			}
			else {
				return -1;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return linkedList.size()-1;
	}
}
