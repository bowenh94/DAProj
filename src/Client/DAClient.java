package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
		BufferedReader in = null;
		DataOutputStream out = null;
		Socket socket = null;

		/*
		 * // socket connect to Leader Socket leaderSocket = null; DataOutputStream
		 * leaderOut = null; BufferedReader leaderIn = null;
		 */

		// read serverlist from file and store in serverlist
		ArrayList<Pair<String, Integer>> serverList = readServerList();

		while (true) {
			try {
				// find the server who is playing in leader role
				for (int i = 0; i < serverList.size(); i++) {
					Pair<String, Integer> firstServer = serverList.get(i);
					try {
						socket = new Socket(firstServer.getKey(), firstServer.getValue());
						out = new DataOutputStream(socket.getOutputStream());
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						JSONObject request = new JSONObject();
						request.put("client_id", clientID);
						request.put("score", client.board.getScore());
						System.out.println(request.toJSONString());
						out.writeUTF(request.toJSONString());
						out.flush();
						System.out.println("flush");
						String resp = in.readLine();
						System.out.println(resp);
						JSONObject response = stringtoJSON(resp);
						if ((Boolean) response.get("respond")) {
							// find leader and got the socket
							break;
						} else {
							// not leader
							socket.close();
							out.close();
							in.close();
							continue;
						}
					} catch (Exception e) {
						System.err.println("Server " + i + " is not alive!");
						continue;
					}
				}
				if (socket == null) {
					System.err.println("No server alive! Query again later!");
					Thread.sleep(REFRESH_INTERVAL);
					continue;
				}

				Thread.sleep(REFRESH_INTERVAL);
				JSONObject msg = new JSONObject();
				// TODO: send msg to server
				msg.put("client_id", clientID);
				msg.put("score", client.board.getScore());
				out.writeUTF(msg.toJSONString());
				out.flush();
				String resp = in.readLine();
				System.out.println(resp);
				JSONObject respond = stringtoJSON(resp);
				if ((boolean) respond.get("respond")) {
					// updating my leader board
					client.board.setLeaderBoard((HashMap<Integer, Integer>) respond.get("leader_board"));
				} else {
					// oops, he is not in leader mode anymore. Should start another query again.
					break;
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

			} catch (Exception e) {
				System.err.println("Connection fail, try again latter~");
				try {
					Thread.sleep(REFRESH_INTERVAL);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
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
			}
		}
	}

	public static JSONObject stringtoJSON(String msg) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			System.out.println(msg+"***************");
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
