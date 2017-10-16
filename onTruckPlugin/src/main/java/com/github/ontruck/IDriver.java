package com.github.ontruck;

public interface IDriver {
	void power(byte value);
	void steer(byte value);
	void brake(byte value);
	byte getLastPowerValue();
}
