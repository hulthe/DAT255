package main.java.it.chalmers.digit;

import java.util.concurrent.*;

public class Main {

	//Path to python scripts (not used yet)
	private static final String PATH = "/etc/onTruck/python";

    public static void main(String[] args) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		Mopy m = new Mopy(threadPoolExecutor);
		Future<String> f = m.getSpeed();
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
