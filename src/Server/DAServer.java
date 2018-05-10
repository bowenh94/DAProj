package Server;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import javafx.util.Pair;

public class DAServer {
	public ArrayList<Pair<String, Integer>> serverList;

	private int currentTerm;
	private int rmiPort = 1099;
	private int serverPort = 8970;
	private String configPath;
	private String serverListPath;
	private int serverNum;
	private int serverId;
	private String url;
	private final int poolsize = 5;
	private ExecutorService eService;

	ServerSocket serverSocket;

	public DAServer(int serverId, int serverPort, int rmiPort, String configPath, String serverListPath)
			throws IOException {
		// Construct a DA server, with port number, rmi register number and basic
		// config.
		this.serverId = serverId;
		this.serverPort = serverPort;
		this.rmiPort = rmiPort;
		this.url = "rmi://localhost:" + this.rmiPort + "/S" + this.serverId;
		this.configPath = configPath;
		this.serverListPath = serverListPath;
		serverList = new ArrayList<>();
		// read serverlist from file and store in serverlist
		readServerList();
		readConfig();

		eService = Executors.newFixedThreadPool(poolsize);

	}

	// Read server list from file and store in serverList
	private void readServerList() {
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

	private void readConfig() {
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

	public int getCurrentTerm() {
		return this.currentTerm;
	}

	public int getServerNum() {
		return this.serverNum;
	}

	public int getServerId() {
		return this.serverId;
	}

	public boolean setCurrentTerm(int term) {
		if (term <= currentTerm)
			return false;
		this.currentTerm = term;
		return true;
	}

	public void startServer() throws RemoteException, MalformedURLException {
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
		// initialize RPCResponse
		RPCResponse.init(serverNum, currentTerm);
		ConsensusModule.initCM(rmiPort, serverId, this);
		RPCImpl RPCServer = new RPCImpl();
		Naming.rebind(this.url, RPCServer);
		RPCImpl.startMode(new FollowerCM());
	}
}

class Handler implements Runnable {
	private JSONObject jsonObject;
	private Socket socket;

	public Handler(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	public void run() {
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

				jsonObject = stringtoJSON(msg);

				/*
				 * Futher inplementation to handling request from client
				 */

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
