package main.java.it.chalmers.digit;

import java.util.concurrent.*;

public class Main {

	private static final String PATH = "/etc/onTruck/python";

    public static void main(String[] args) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		MopedController m = Mopy.getInstance();
		RunnableFuture<String> f = m.getSpeed();
		threadPoolExecutor.execute(f);
		while(!f.isDone()){
			System.out.println("not done yet");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			System.out.println(f.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		threadPoolExecutor.shutdown();
	}
}
