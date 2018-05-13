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

import javax.swing.JFrame;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.util.Pair;

public class DAClient extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
	static private String serverListPath = "src/configs/serverList.txt";
	public static int clientID;
	private static int REFRESH_INTERVAL = 1;

	public DAClient() {
		board = new Board();
		add(board);

		setResizable(false);
		pack();

		setTitle("Snake");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		DAClient client = new DAClient();
		clientID = Integer.parseInt(args[0]);
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
		ArrayList<Pair<String, Integer>> serverList = readServerList();

		while (true) {
			// find the server who is playing in leader role
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.err.println("FUCKING sleep error ");
				//e1.printStackTrace();
			}
			for (int i = 0; i < serverList.size(); i++) {
				Pair<String, Integer> firstServer = serverList.get(i);
				System.out.println("Connect to " + firstServer.getKey() + ":" + firstServer.getValue());

				try {
					socket = new Socket(firstServer.getKey(), firstServer.getValue());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					System.out.println("Server " + firstServer.getKey() + ":" + firstServer.getValue() + " is not alive!");
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
					e.printStackTrace();
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
					System.out.println("Server " + firstServer.getKey() + ":" + firstServer.getValue() + " is not leader now. Reconnect to another server.");
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


				/*
				 * not used any more, feel sad (Owen)
				 * 
				 * // get leader info while (true) { JSONObject query = new JSONObject();
				 * query.put("command", "QUERY_LEADER"); out.writeUTF(query.toJSONString());
				 * out.flush();
				 * 
				 * String resp = in.readLine(); JSONObject jsonObject = stringtoJSON(resp);
				 * 
				 * if ((boolean) jsonObject.get("respond")) { String ip = (String)
				 * jsonObject.get("leader_ip"); int port = (int) jsonObject.get("leader_port");
				 * try { leaderSocket = new Socket(ip, port);
				 * System.err.println("Connect to Leader" + ip + ":" + port); break; } catch
				 * (IOException e) { System.err.println("leader is fail!"); } } // there is no
				 * leader currently, // waiting for a new election
				 * Thread.sleep(REFRESH_INTERVAL); }
				 * 
				 * // Connect to leader successful! // Start to send client score and get leader
				 * board leaderOut = new DataOutputStream(leaderSocket.getOutputStream());
				 * leaderIn = new BufferedReader(new
				 * InputStreamReader(leaderSocket.getInputStream())); JSONObject msg = new
				 * JSONObject();
				 * 
				 * while (true) { Thread.sleep(REFRESH_INTERVAL);
				 * 
				 * // TODO: send msg to server msg.put("command", "SCORE_MSG"); msg.put("score",
				 * client.board.getScore());
				 * 
				 * leaderOut.writeUTF(msg.toJSONString()); out.flush();
				 * 
				 * String resp = leaderIn.readLine(); JSONObject jsonResp = stringtoJSON(resp);
				 * client.board.setLeaderBoard((HashMap<Integer, Integer>)
				 * jsonResp.get("leader_board")); }
				 */
				/*
			} finally {
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
				/*
				 * try { if (leaderSocket != null) { leaderSocket.close(); } } catch
				 * (IOException e) { e.printStackTrace(); } try { if (leaderSocket != null) {
				 * leaderSocket.close(); } } catch (IOException e) { e.printStackTrace(); } if
				 * (leaderSocket != null) { try { leaderSocket.close(); } catch (IOException e)
				 * { e.printStackTrace(); } } if (leaderSocket != null) { try {
				 * leaderSocket.close(); } catch (IOException e) { e.printStackTrace(); } }
				 */

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
	private static ArrayList<Pair<String, Integer>> readServerList() {
		ArrayList<Pair<String, Integer>> serverList = new ArrayList<>();
		File file = new File(serverListPath);
		BufferedReader bReader;
		String line;
		try {
			bReader = new BufferedReader(new FileReader(file));
			while ((line = bReader.readLine()) != null) {
				System.out.println(line);
				String[] nServer = line.split(":");
				Pair<String, Integer> nPair = new Pair<String, Integer>(nServer[0], Integer.parseInt(nServer[1]));
				serverList.add(nPair);
			}
			bReader.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serverList;
	}
}
