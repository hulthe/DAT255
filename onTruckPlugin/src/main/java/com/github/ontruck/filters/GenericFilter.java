package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;

public class GenericFilter extends StateFilter {

	private final IDriver driver;
	private final MopedState desiredState;

	public GenericFilter(IDriver driver, MopedState desiredState) {
		this.driver = driver;
		this.desiredState = desiredState;
	}

	@Override
	public void power(byte value) {
		if(desiredState.equals(this.getState())) {
			driver.power(value);
		}
	}

	@Override
	public void steer(byte value) {
		if(desiredState.equals(this.getState())) {
			driver.steer(value);
		}
	}

	@Override
	public void brake(byte value) {
		if(desiredState.equals(this.getState())) {
			driver.brake(value);
		}
	}
}
