package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Server.newServer.CmMode;
import javafx.util.Pair;

public class newServer {
	public static int serverId;
	public static int initServerPort;
	public static int initRmiPort;
	public static int serverState;
	public static int serverPort;
	public static int rmiPort;
	public static Log log;
	public static int serverNum;
	public static int currentTerm;
	public static StateMachine stateMachine;

	public static String name;
	private static final int poolsize = 5;
	private static ExecutorService eService;
	private static ServerSocket serverSocket;
	/*
	 * change to leader for test of c-s communication
	 */
	public static CmMode mode = CmMode.FOLLOWER;
	public static int votedFor = -1;

	//For single client and server test
	//public static int test = 3;
	
	public enum CmMode {
		LEADER, FOLLOWER, CANDIDATE
	}

	private static String configPath = "src/configs/init.config";
	private static String serverListPath = "src/configs/serverList.txt";
	private static ArrayList<Pair<String, Integer>> serverList = new ArrayList<>();

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("usage: serverID, initServerPort, initRmiPort");
			System.exit(0);
		}
		// Read arguments
		serverId = Integer.parseInt(args[0]);
		initServerPort = Integer.parseInt(args[1]);
		initRmiPort = Integer.parseInt(args[2]);
		serverPort = initServerPort + serverId;
		rmiPort = initRmiPort + serverId;
		// name for rmi binding
		name = "S" + serverId;
		
		log = new Log();
		stateMachine = new StateMachine();
		// read file
		readServerList();
		readConfig();
		
		
		
		// create thread pool and start a service thread
		eService = Executors.newFixedThreadPool(poolsize);
		new Thread() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(serverPort);
					Socket socket = null;
					while (true) {
						socket = serverSocket.accept();
						eService.execute(new Handler(socket));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}.start();
		
		
		// consensus module
		RPCResponse.init(serverNum, currentTerm);
		ConsensusModule.initCM(rmiPort, serverId);
		RPCImpl RPCServer;
		try {
			RPCServer = new RPCImpl();
			Registry registry = LocateRegistry.createRegistry(rmiPort);
			registry.rebind(name, RPCServer);
			System.out.println("S"+newServer.serverId + " bind " + name + " to port "+rmiPort);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RPCImpl.startMode(new FollowerCM());
	
	}

	private static void readServerList() {
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
	}

	private static void readConfig() {
		File file = new File(configPath);
		BufferedReader bReader;
		String line;
		try {
			bReader = new BufferedReader(new FileReader(file));
			while ((line = bReader.readLine()) != null) {
				// System.out.println(line);

				String[] nConfig = line.split("=");

				if (nConfig[0].equals("NUMBER_SERVERS"))
					serverNum = Integer.parseInt(nConfig[1]);
				else if (nConfig[0].equals("CURRENT_TERM"))
					currentTerm = Integer.parseInt(nConfig[1]);
				else
					System.out.println("Wrong format for config file!");
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

	public static boolean setCurrentTerm(int term) {
		if (term <= currentTerm)
			return false;
		currentTerm = term;
		return true;
	}

}

class Handler implements Runnable {
	private JSONObject request;
	private JSONObject response;
	private Socket socket;

	public Handler(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		System.out.println("new req comes in");
		DataInputStream br = null;
		DataOutputStream out = null;

		try {
			out = new DataOutputStream(socket.getOutputStream());
			String msg = null;
			PushbackInputStream pbi = new PushbackInputStream(socket.getInputStream());
			br = new DataInputStream(pbi);
			int singlebyte;
			if ((singlebyte = pbi.read()) != -1) {

				pbi.unread(singlebyte);
				msg = br.readUTF();
				System.out.println("get message" + msg);
				request = stringtoJSON(msg);
				
				response = new JSONObject();
				/*
				 * Futher inplementation to handling request from client
				 */
				if (newServer.mode == CmMode.LEADER) {
					// if it's leader, append client's id and score to log 
					int client_id = ((Long) request.get("client_id")).intValue();
					int client_score = ((Long) request.get("score")).intValue();
					Entry new_e = new Entry(newServer.currentTerm,client_id, client_score);
					System.out.println(new_e);
					newServer.log.append(new_e);

					// read log file
					response.put("reply", "TRUE"); // YES, I'm leader
					JSONObject leaderBoard = newServer.stateMachine.executeLog(newServer.log, ConsensusModule.cmLastCommitId, socket);
					
					// Test code for single server and client
					//JSONObject leaderBoard = newServer.stateMachine.executeLog(newServer.log, newServer.test, socket);
					//newServer.test+=1;
					
					System.out.println(leaderBoard.toJSONString());
					response.put("leader_board", leaderBoard.toJSONString());
					out.writeUTF(response.toJSONString());
					out.flush();
				} else {
					// i'm not in leader role
					response.put("reply", "FALSE"); // Sorry, I'm not leader
					out.writeUTF(response.toJSONString());
					out.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static JSONObject stringtoJSON(String msg) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(msg);
			return jsonObject;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
