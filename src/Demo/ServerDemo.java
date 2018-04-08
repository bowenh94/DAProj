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
	private static String localhost = "127.0.0.1";
	
	private static boolean isbootstrap = false;
	
	public static void main(String[] args) throws IOException, ParseException {
		// generate 3 servers on local machine, start with arguement port number
		genServerList(8957);
		
    	// Command line options: bootstrap
    	Options options = new Options();
    	options.addOption("bootstrap",false,"Config this server as bootstrap server");
    	
    	CommandLineParser parser = new DefaultParser();
    	CommandLine commandLine = parser.parse(options, args);
    	
    	if(commandLine.hasOption("bootstrap"))
    		isbootstrap = true;
		
		DAServer daServer = new DAServer(isbootstrap, serverList);
		daServer.run();
	}
	
	private static void genServerList(int port){
		for(int i=port;i<port+3;i++){
			Pair<String, Integer> server = new Pair<String, Integer>(localhost, i);
			serverList.add(server);
		}
	}

}
