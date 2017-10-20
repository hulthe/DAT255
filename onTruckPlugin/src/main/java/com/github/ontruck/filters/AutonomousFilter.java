package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;


public class AutonomousFilter extends StateFilter {

	private final IDriver driver;

	public AutonomousFilter(IDriver driver) {
		this.driver = driver;
	}


	@Override
	public void power(byte value) {
		if(
			MopedState.AdaptiveCruiseControl.equals(getState()) // add more when platooning is implemented
		) {
			driver.power(value);
		}
	}

	@Override
	public void steer(byte value) {
		// add more when platooning is implemented
	}

	@Override
	public void brake(byte value) {
		if(
			MopedState.AdaptiveCruiseControl.equals(getState()) // add more when platooning is implemented
		) {
			driver.brake(value);
		}
	}

	@Override
	public void increaseSpeed() {
		if(
			MopedState.AdaptiveCruiseControl.equals(getState()) // add more when platooning is implemented
			) {
			driver.increaseSpeed();
		}
	}

	@Override
	public void decreaseSpeed() {
		if(
			MopedState.AdaptiveCruiseControl.equals(getState()) // add more when platooning is implemented
			) {
			driver.decreaseSpeed();
		}
	}

	@Override
	public byte getLastPowerValue() {
		return driver.getLastPowerValue();
	}
}
