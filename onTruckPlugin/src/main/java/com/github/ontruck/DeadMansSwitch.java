package com.github.ontruck;

// Stops car if connection is dropped
public class DeadMansSwitch extends Thread {

	private static final int DELAY = 100; // Check interval

	// Time before car should automatically brake. (Note that actual longest time before emergency stop is TIMEOUT + DELAY)
	private static final long TIMEOUT = 750;

	private final IDriver driver;
	private long lastTime;

	public DeadMansSwitch(IDriver driver) {
		this.driver = driver;
		lastTime = System.currentTimeMillis();
		this.setDaemon(true); // Make sure thread closes when application does.
	}

	// Tell DeadMansSwitch that you're still alive
	public void ping() {
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while(true) { // Run until interrupted

			if(lastTime + TIMEOUT < System.	currentTimeMillis()) { // If more time than TIMEOUT has passed
				// System.out.println("No connection, Braking!");
				driver.brake((byte)255);
			}

			try {
				Thread.sleep(DELAY);
			} catch(InterruptedException e) {
				break;
			}
		}
	}
}
