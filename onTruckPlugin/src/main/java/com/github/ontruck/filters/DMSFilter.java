package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;

public class DMSFilter extends StateFilter {
	private IDriver driver;

	public DMSFilter(IDriver driver) {
		this.driver = driver;
	}

	@Override
	public void power(byte value) {}

	@Override
	public void steer(byte value) {}

	@Override
	public void brake(byte value) {
		if(
			MopedState.Manual.equals(getState()) ||
			MopedState.CruiseControl.equals(getState()) ||
			MopedState.AdaptiveCruiseControl.equals(getState())
		) {
			driver.brake(value);
		}
	}

	@Override
	public void increaseSpeed() {
		driver.increaseSpeed();
	}

	@Override
	public void decreaseSpeed() {
		driver.decreaseSpeed();
	}

	@Override
	public byte getLastPowerValue() {
		return driver.getLastPowerValue();
	}
}
