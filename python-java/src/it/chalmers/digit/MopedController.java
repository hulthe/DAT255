package it.chalmers.digit;

import java.util.concurrent.Future;

public interface MopedController {
	Future<String> getSpeed();
	Future<Void> setSpeed(int speed);
	Future<Void> steer(double steeringVector);
	Future<String> getFrontSensorDistance();
	Future<Void> stop();

}
