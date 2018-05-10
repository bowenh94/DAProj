package Server;

import java.util.Timer;
import java.util.LinkedList;
import java.util.Random;

public class FollowerCM extends ConsensusModule{

	private Timer timeoutTimer;
	private int TIMEOUT_TIMER_ID = 1;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// TODO Auto-generated method stub
		synchronized(cmLock) {
			System.out.println("Server " + cmDAServer.getServerId());
			this.resetTimeoutTimer();
			int term = cmDAServer.getCurrentTerm();
			if(leaderTerm>=term) {
				cmDAServer.setCurrentTerm(term);
			}
			int termAtLastIndex=log.getLastTerm();
			if(termAtLastIndex==prevLogTerm) {
				if(prevLogIndex==(log.get().size()-1)) {
					for(Entry entry : entries) {
						log.get().add(entry);
					}
				}
				else if(entries==null) {
					return -1;
				}
				else if((prevLogIndex == -1) || ((log.getEntry(prevLogIndex) != null) && (log.getLastTerm() == prevLogTerm))){
					LinkedList<Entry> tmpEntries = new LinkedList<Entry> ();
					for (int i=0; i<=prevLogIndex; i++) {
					  Entry entry = log.getEntry(i);
					  tmpEntries.add (entry);
					}
					for(Entry entry : entries) {
						tmpEntries.add(entry);
					}
					log.setLog(tmpEntries);
				}
				else {
					return -1;
				}
			}
		}
		return log.get().size();
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		synchronized(cmLock) {
			int term = cmDAServer.getCurrentTerm();
			if(candidateTerm>=term && cmDAServer.getServerId()==0 && lastLogIndex>=cmLastCommitId) {
				System.out.println("Server " + cmServerId + "received vote request from server " + candidateID +" and vote");
				cmDAServer.setCurrentTerm(candidateTerm);
				return 0;
			}
			else {
				cmDAServer.setCurrentTerm(candidateTerm);
				return term;
			}
		}
	}

	@Override
	protected void handleTimeout(int timerId) {
		// TODO Auto-generated method stub
		synchronized(cmLock) {
			if(timerId == this.TIMEOUT_TIMER_ID) {
				timeoutTimer.cancel();
				System.out.println(cmServerId + " has not received heartbeat from leader");
				RPCImpl.startMode(new CandidateCM());
			}
		}
	}
	
	private void resetTimeoutTimer() {
		timeoutTimer.cancel();
		Random random = new Random();
		timeoutTimer = scheduleTimer(random.nextInt(this.ELECTION_TIMEOUT_MAX - this.ELECTION_TIMEOUT_MIN) + this.ELECTION_TIMEOUT_MIN, this.TIMEOUT_TIMER_ID);
	}
	
}
