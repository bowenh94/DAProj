package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javafx.util.Pair;


public class DAServer {
	public ArrayList<Pair<String, Integer>> serverList;
	

	private String IPaddress = "127.0.0.1"; 
	private int port = 6666;
	private boolean isBootstrap;
	
	public DAServer(boolean isBootstrap, ArrayList<Pair<String,Integer>> serverList) throws IOException {
		// TODO Auto-generated constructor stub
		this.isBootstrap = isBootstrap;
		this.serverList = serverList;
		this.builder(IPaddress, port);
	}
	private void builder(String address, int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		if(isBootstrap){
			this.bootstrap();
		}
		else{
			serverSocket.accept();
		}
		
	}

	private void bootstrap() {
		//Bcast start msg 
	}
	public void run(){
		
	}
}
