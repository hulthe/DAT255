package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;

public class ManualFilter extends StateFilter implements IDriver {

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
}
