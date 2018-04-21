package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONObject;

public class StateMachine {
	private HashMap<Integer, Integer> map;
	public StateMachine() {
		// TODO Auto-generated constructor stub
		this.map = new HashMap<>();
	}
	/*
	 * get log and client socket, execute log to get leader board, convert it to JSONString and send back. 
	 */
	@SuppressWarnings("unchecked")
	public void executeLog(Log log, Socket socket) {
		LinkedList<Entry> smlog = log.get();
		for(Entry e:smlog){
			int clientId = e.getClientID();
			int clientVal = e.getCLientVal();
			if(map.containsKey(clientId)){
				map.replace(clientId, clientVal);
			}
			else
				map.put(clientId, clientVal);
		}
		
		JSONObject leaderBoard = new JSONObject();
		leaderBoard.putAll(map);
		DataOutputStream dataOut = null;
		try {
			dataOut = new DataOutputStream(socket.getOutputStream());
			dataOut.writeUTF(leaderBoard.toJSONString());
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, Integer> get() {
		return this.map;
	}
}
