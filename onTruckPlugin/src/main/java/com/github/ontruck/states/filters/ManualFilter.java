package com.github.ontruck.states.filters;

import com.github.ontruck.driver.IDriver;
import com.github.ontruck.states.MopedState;
import com.github.ontruck.states.StateFilter;


public class ManualFilter extends StateFilter {

	private final IDriver driver;

	public ManualFilter(IDriver driver) {
		this.driver = driver;
	}


	@Override
	public void power(byte value) {
		if(
			MopedState.Manual.equals(getState()) ||
			MopedState.CruiseControl.equals(getState())
		) {
			driver.power(value);
		}
	}

	@Override
	public void steer(byte value) {
		if(
			MopedState.Manual.equals(getState()) ||
			MopedState.CruiseControl.equals(getState()) ||
			MopedState.AdaptiveCruiseControl.equals(getState())
		) {
			driver.steer(value);
		}
	}

	@Override
	public void brake(byte value) {
		if(
			MopedState.Manual.equals(getState()) ||
			MopedState.CruiseControl.equals(getState())
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
