package Server;

import java.util.Timer;
import java.util.Random;
import java.util.TimerTask;

public class FollowerCM extends ConsensusModule{

	private Timer timeoutTimer;
	
	private void resetTimeoutTimer() {
		timeoutTimer.cancel();
		Random random = new Random();
	}
	
	private Timer configTimer(long milli) {
		Timer timer = new Timer(false);
		TimerTask timerTask = new TimerTask() {
			public void run() {
				FollowerCM.this.handleTimeout();
			}
		};
		timer.schedule(timerTask, milli);
		return timer;
	}
	
	private void handleTimeout() {
		timeoutTimer.cancel();
		ConsensusModule.startMode(new CandidateCM());
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Random random = new Random();
		timeoutTimer = configTimer(random.nextInt(1000));
	}

	@Override
	public int appendEntries(int leaderTerm, int leaderID, int prevLogIndex, int prevLogTerm, Entry[] entries,
			int leaderCommit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int requestVote(int candidateTerm, int candidateID, int lastLogIndex, int lastLogTerm) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
