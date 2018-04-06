package Demo;

import java.io.IOException;

import Server.DAServer;

public class ServerDemo {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DAServer daServer = new DAServer(false);
		daServer.run();
	}

}
