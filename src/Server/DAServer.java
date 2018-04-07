package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class DAServer {
	public ArrayList<String> serverList = new ArrayList<String>();

	private String IPaddress = "127.0.0.1";
	private int port = 8759;
	private boolean isBootstrap = false;
	private ServerSocket serverSocket;
	private ConsensusModule consensusModule;

	public DAServer(boolean isBootstrap) throws IOException {
		// TODO Auto-generated constructor stub
		this.isBootstrap = isBootstrap;
		this.builder(IPaddress, port);
	}

	private void builder(String address, int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.consensusModule = new ConsensusModule();
		if (isBootstrap) {
			this.bootstrap();
		} else {
			serverSocket.accept();
		}

	}

	private void genServerlist() {
		this.serverList.add("192.168.0.1:9900");
	}
	
	private void bootstrap() {
		this.genServerlist();
		// Bcast start msg
	}

	public void run() {
		switch(consensusModule.getState()) {
			case Leader:
				consensusModule.sendHeartBeat();
			default:
				break;
		}
	}
}
