package com.github.ontruck.moped;

import com.github.ontruck.driver.IDriver;

public class MockDriver implements IDriver {


	@Override
	public void power(byte value) {

	}

	@Override
	public void steer(byte value) {

	}

	@Override
	public void brake(byte value) {

	}

	@Override
	public void increaseSpeed() {

	}

	@Override
	public void decreaseSpeed() {

	}

	@Override
	public byte getLastPowerValue() {
		return 0;
	}
}
