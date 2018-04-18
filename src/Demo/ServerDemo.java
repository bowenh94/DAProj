package Demo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Server.DAServer;
import javafx.util.Pair;

public class ServerDemo {
	
	private static ArrayList<Pair<String, Integer>> serverList;
	private static String configPath = "src/configs/init.config";
	private static String serverListPath = "src/configs/serverList.txt";
	private static int serverPort = 8899;
	private static int rmiPort = 1099; 
	
	
	public static void main(String[] args){
		// generate servers from file of serverList
		
		
    	// Command line options: bootstrap
    	Options options = new Options();
    	
    	

		DAServer daServer = new DAServer(serverPort, rmiPort, configPath, serverListPath);
		daServer.run();
	}
	

}
