import java.util.Random;

public class PointlessThread extends Thread {
	
	public PointlessThread() {
		super();
	}
	
	public void run() {
		Random random = new Random();
		
		final int TEN_THOUSAND = 10000;
		
		int upperLimit = random.nextInt() * TEN_THOUSAND;
		
		for(int i = 0; i < upperLimit; i++);
	}

}
