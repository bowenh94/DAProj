package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
	public int clientID;
	private static int REFRESH_INTERVAL = 5;

	public DAClient() {
		board = new Board();
		add(board);

		setResizable(false);
		pack();

		setTitle("Snake");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		DAClient client = new DAClient();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame ex = client;
				ex.setVisible(true);
			}
		});

		// Main thread: communicate with server
		JSONObject jsonObject;
		DataInputStream br = null;
		DataOutputStream out = null;
		Socket socket = null;
		ArrayList<Pair<String, Integer>> serverList;
		// read serverlist from file and store in serverlist
		serverList = readServerList();

		try {
			// find one server is alive
			for (int i = 0; i < serverList.size(); i++) {
				Pair<String, Integer> firstServer = serverList.get(i);
				try {
					socket = new Socket(firstServer.getKey(), firstServer.getValue());
				} catch (Exception e) {
					System.err.println("Server " + i + " is not exist!");
					continue;
				}
			}

			if (socket == null) {
				System.err.println("No server alive! Client is offline!");
				return;
			}

			out = new DataOutputStream(socket.getOutputStream());
			PushbackInputStream pbi = new PushbackInputStream(socket.getInputStream());
			br = new DataInputStream(pbi);
			String msg = null;

			int singlebyte;
			// get Leader information
			if ((singlebyte = pbi.read()) != -1) {

				pbi.unread(singlebyte);
				msg = br.readUTF();

				jsonObject = stringtoJSON(msg);
				/*
				 * TODO: Futher inplementation to send request to server
				 */

			}

			// start to send socore and get leader board
			while (true) {
				Thread.sleep(REFRESH_INTERVAL);
				// TODO: send msg to server
				int score = client.board.getScore();
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
