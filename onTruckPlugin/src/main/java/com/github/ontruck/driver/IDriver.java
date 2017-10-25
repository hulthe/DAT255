package com.github.ontruck.driver;

public interface IDriver {
	/**
	 * Send power value to the motor
	 * @param value -127 (full reverse) to 127 (full speed ahead (aye aye captain))
	 */
	void power(byte value);

	/**
	 * Send angle value to the steering servo
	 * @param value -127 (full to port) to 127 (full to starboard)
	 */
	void steer(byte value);

	/**
	 * Send power value to the brakes
	 * @param value 0 (no braking power) to 255 (full braking power)
	 */
	void brake(byte value);

	void increaseSpeed();

	void decreaseSpeed();

	/**
	 * @return The last motor power value set
	 */
	byte getLastPowerValue();
}
