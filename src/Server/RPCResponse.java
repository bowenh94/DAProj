package Server;

/*
 * RPC Response, bind to term, each term start with two empty list,
 * 
 */
public class RPCResponse {
	// format of aER and vRR is list[serverId]=thisServerResp
	private static Integer[] appendEntryResp;
	private static int[] voteReqResp;
	private static int cTerm;
	
	public static void init(int num_servers, int term) {
		// TODO Auto-generated constructor stub
		cTerm = term;
		appendEntryResp = new Integer[num_servers];
		clearAppendResp(term);
		voteReqResp = new int[num_servers];
		clearVote(term);
	}
	
	public static void setTerm(int term){
		cTerm = term;
	}
	
	public static boolean clearVote(int term) {
		if(term == cTerm){
			for(int i=0;i<voteReqResp.length;i++)
				voteReqResp[i] = -1;
			return true;
		}else 
			return false;
	}
	
	public static boolean clearAppendResp(int term) {
		if(term==cTerm){
			for(int i=0;i<appendEntryResp.length;i++)
				appendEntryResp[i] = -100;
			return true;
		}else
			return false;
	}
	
	public static boolean setVote(int serverId, int response, int term) {
		if(term == cTerm){
			voteReqResp[serverId] = response;
			return true;
		}else
			return false;
	}	
	
	public static boolean setAppendEntryResp(int serverId, int response, int term){
		if(term == cTerm){
			appendEntryResp[serverId] = response;
			return true;
		}else
			return false;
	}
	
	public static Integer[] getAppendEntryResp(int term) {
		if(term == cTerm)
			return appendEntryResp;
		else
			return null;
	}
	public static int[] getVoteResp(int term) {
		if(term == cTerm)
			return voteReqResp;
		else 
			return null;		
	}
	
}
