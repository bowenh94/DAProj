package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Pair;

public class DAServer {
	public ArrayList<Pair<String,Integer>> serverList;

	private int rmiPort=1099;
	private int serverPort=8970;
	private String configPath;
	private String serverListPath;
	private int serverNum;
	private int currentTerm;
	
	public DAServer(int serverPort, int rmiPort, String configPath, String serverListPath) throws IOException {
		// Construct a DA server, with port number, rmi register number and basic config. 
		this.serverPort = serverPort;
		this.rmiPort = rmiPort;
		this.configPath = configPath;
		this.serverListPath = serverListPath;
		serverList = new ArrayList<>();
		// read serverlist from file and store in serverlist
		readServerList();
		readConfig();
	}


	// Read server list from file and store in serverList
	private void readServerList(){
		File file = new File(serverListPath);
		BufferedReader bReader;
		String line;
		try {
			bReader = new BufferedReader(new FileReader(file));
			while((line = bReader.readLine())!=null){
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
	private void readConfig(){
		File file = new File(configPath);
		BufferedReader bReader;
		String line;
		try {
			bReader = new BufferedReader(new FileReader(file));
			while((line = bReader.readLine())!=null){
				System.out.println(line);
				String[] nConfig = line.split("=");
				if(nConfig[0]=="NUMBER_SERVERS")
					serverNum = Integer.parseInt(nConfig[1]);
				if(nConfig[0]=="CURRENT_TERM")
					currentTerm = Integer.parseInt(nConfig[1]);
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
	
	public void startServer() {
		
		ConsensusModule.startMode(new FollowerCM());
	}
}
