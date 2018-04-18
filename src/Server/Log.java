package Server;

import java.util.LinkedList;

import javafx.util.Pair;

public class Log {
	private LinkedList<Entry> linkedList;

	public Log() {
		// TODO Auto-generated constructor stub
		this.linkedList = new LinkedList<>();
	}

	public LinkedList<Entry> get() {
		return this.linkedList;
	}

	public void append(Entry e) {
		this.linkedList.add(e);
	}
}
