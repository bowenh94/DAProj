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
	public ArrayList<Pair<String, Integer>> serverList;
	public int clientID;

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

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame ex = new DAClient();
				ex.setVisible(true);
			}
		});

		new Thread() {
			@Override
			public void run() {
				JSONObject jsonObject;
				DataInputStream br = null;
				DataOutputStream out = null;
				Socket socket = null;
				try {
					ArrayList<Pair<String, Integer>> serverList = new ArrayList<>();
					// read serverlist from file and store in serverlist
					serverList = readServerList();
					Pair<String, Integer> firstServer = serverList.get(0);

					socket = new Socket(firstServer.getKey(), firstServer.getValue());
					out = new DataOutputStream(socket.getOutputStream());
					PushbackInputStream pbi = new PushbackInputStream(socket.getInputStream());
					br = new DataInputStream(pbi);

					String msg = null;
					int singlebyte;
					if ((singlebyte = pbi.read()) != -1) {

						pbi.unread(singlebyte);
						msg = br.readUTF();

						jsonObject = stringtoJSON(msg);
						/*
						 * Futher inplementation to send request to server
						 */

					}
					// find Leader

					while (true) {
						Thread.sleep(5);

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

		}.start();

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
	static private ArrayList<Pair<String, Integer>> readServerList() {
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
