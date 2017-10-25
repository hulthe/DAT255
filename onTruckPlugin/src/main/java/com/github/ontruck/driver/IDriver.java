package com.github.ontruck.driver;

public interface IDriver {
	void power(byte value);
	void steer(byte value);
	void brake(byte value);
	void increaseSpeed();
	void decreaseSpeed();
	byte getLastPowerValue();
}
