package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class DAServer {
	public ArrayList<String> serverList = new ArrayList<String>();

	private int rmiPort;
	private int serverPort;
	private String configPath;
	private String serverListPath;

	public DAServer(int serverPort, int rmiPort, String configPath, String serverListPath){
		// TODO Auto-generated constructor stub
		this.serverPort = serverPort;
		this.rmiPort = rmiPort;
		this.configPath = configPath;
		this.serverListPath = serverListPath;
		
		readServerList();
	}


	private static void readServerList(){
		
	}
	
	public void run(){
		ConsensusModule.startMode(new FollowerCM());
	}
}
