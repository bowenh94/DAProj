package Server;

import java.util.LinkedList;

import javafx.util.Pair;

public class Log {
	private LinkedList<Pair<Integer,Pair<Integer, Integer>>> linkedList;
	public Log() {
		// TODO Auto-generated constructor stub
		this.linkedList = new LinkedList<>();
	}
	public LinkedList<Pair<Integer,Pair<Integer, Integer>>> get() {
		return this.linkedList;
	}
	public boolean append(Pair<Integer,Pair<Integer, Integer>> e) {
		this.linkedList.add(e);
		return true;
	}
	
}
