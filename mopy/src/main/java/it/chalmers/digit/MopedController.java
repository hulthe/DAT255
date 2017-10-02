package main.java.it.chalmers.digit;

import java.util.concurrent.RunnableFuture;

/**
 * MopedController
 * Java interface for the methods necessary in Mopy.java which set and gets information form the python files.
 */
public interface MopedController {
	RunnableFuture<String> getSpeed();
	RunnableFuture<Void> setSpeed(int speed);
	RunnableFuture<Void> steer(double steeringVector);
	RunnableFuture<String> getFrontSensorDistance();
	RunnableFuture<Void> stop();

}
