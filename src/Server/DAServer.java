package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class DAServer {
	public ArrayList<String> serverList = new ArrayList<String>();
	

	private String IPaddress = "127.0.0.1"; 
	private int port = 8759;
	private boolean isBootstrap = false;
	
	public DAServer(boolean isBootstrap) throws IOException {
		// TODO Auto-generated constructor stub
		this.isBootstrap = isBootstrap;
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
	private void genServerlist(){
		this.serverList.add("192.168.0.1:9900");
	}
	private void bootstrap() {
		this.genServerlist();
		//Bcast start msg 
	}
	public void run(){
		
	}
}
