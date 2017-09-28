package main.java.it.chalmers.digit;

import java.util.concurrent.RunnableFuture;

public interface MopedController {
	RunnableFuture<String> getSpeed();
	RunnableFuture<Void> setSpeed(int speed);
	RunnableFuture<Void> steer(double steeringVector);
	RunnableFuture<String> getFrontSensorDistance();
	RunnableFuture<Void> stop();

}
