package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.util.Pair;

@SuppressWarnings("restriction")
public class DAClient extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
	static private String serverListPath = "src/configs/serverList.txt";
	public static int clientID;
	private static int REFRESH_INTERVAL = 100;
	public static Map<Integer, String> serverList = new HashMap<Integer, String>();
	public static int initPort = 8890;
	public DAClient() {
		board = new Board();
		add(board);
		
		setResizable(false);
		pack();

		setTitle("Snake-Client" + clientID);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
//	public DAClient(int clientID) {
//		board = new Board(clientID);
//		add(board);
//
//		setResizable(false);
//		pack();
//
//		setTitle("Snake");
//		setLocationRelativeTo(null);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		clientID = Integer.parseInt(args[0]);
		DAClient client;
		client = new DAClient();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame ex = client;
				ex.setVisible(true);
			}
		});

		// Main thread: communicate with server

		// socket to find a Leader
		DataInputStream in = null;
		DataOutputStream out = null;
		Socket socket = null;

		/*
		 * // socket connect to Leader Socket leaderSocket = null; DataOutputStream
		 * leaderOut = null; BufferedReader leaderIn = null;
		 */

		// read serverlist from file and store in serverlist
		readServerList();

		while (true) {
			// find the server who is playing in leader role
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.err.println("FUCKING sleep error ");
				//e1.printStackTrace();
			}
			for (int i = 0; i < serverList.size(); i++) {
				String firstServer = serverList.get(i);
				System.out.println("Connect to " + firstServer + ":" + (initPort+i));

				try {
					socket = new Socket(firstServer, (initPort+i));
				} catch (UnknownHostException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					System.out.println("Server " + firstServer + ":" + (initPort+i) + " is not alive!");
					continue;
				}

				// generate request JSON object
				JSONObject request = new JSONObject();
				request.put("client_id", clientID);
				request.put("score", client.board.getScore());
				System.out.println(request.toJSONString());
				
				String resp;
				try {
					out = new DataOutputStream(socket.getOutputStream());
					in = new DataInputStream(socket.getInputStream());
					out.writeUTF(request.toJSONString());
					out.flush();
					resp = in.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					continue;
				}

				JSONObject response = stringtoJSON(resp);
				String reply = (String) response.get("reply");
				System.out.println(response.toJSONString()+"!!!!!!!!!!!!!!!!!");
				
				if("TRUE".equals(reply)){
					JSONObject leaderboard = stringtoJSON(response.get("leader_board").toString());
					
					System.out.println(leaderboard.toJSONString());
					
					client.board.setLeaderBoard(leaderboard);

				} else {
					// current server is not leader
					System.out.println("Server " + firstServer + ":" + (initPort+i) + " is not leader now. Reconnect to another server.");
					try {
						socket.close();
						out.close();
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}				
				try {
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (in != null)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (socket == null) {
				System.err.println("There is no any server alive!");
				try {
					Thread.sleep(REFRESH_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	// end main
	}


	public static JSONObject stringtoJSON(String msg) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(msg);
			return jsonObject;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("FUCK");
			e.printStackTrace();
		}
		return null;
	}

	// Read server list from file and store in serverList
	private static void readServerList() {
		File file = new File(serverListPath);
		BufferedReader bReader;
		String line;
		try {
			bReader = new BufferedReader(new FileReader(file));
			while ((line = bReader.readLine()) != null) {
				System.out.println(line);
				String[] inLine = line.split(",");
				
				int sID = Integer.parseInt(inLine[0]);
				String[] nServer = inLine[1].split(":");
				String ipAddress = nServer[0];
				if(sID==0)
					initPort = Integer.parseInt(nServer[1]);
				
				serverList.put(sID, ipAddress);
			}
			bReader.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
