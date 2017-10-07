package com.github.ontruck;

public class DeadMansSwitch implements Runnable {

	private static final int DELAY = 50;
	private static final long TIMEOUT = 200;

	private final DriveProtocol driver;
	private long lastTime;

	public DeadMansSwitch(DriveProtocol driver) {
		this.driver = driver;
		lastTime = System.currentTimeMillis();
	}

	public void ping() {
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while(true) {

			if(lastTime + TIMEOUT < System.	currentTimeMillis()) {
				System.out.println("No connection, Braking!");
				driver.emergencyStop();
			}

			try {
				Thread.sleep(DELAY);
			} catch(InterruptedException e) {
				break;
			}
		}
	}
}
