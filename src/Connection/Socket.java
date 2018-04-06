package Connection;

public class Socket {
	private int port;
	private String ip;
	private Socket socket;
	public Socket(String ip, int port) {
		// TODO Auto-generated constructor stub
		this.ip = ip;
		this.port = port;
		this.socket = new Socket(ip, port);
	}
}
