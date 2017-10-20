package com.github.ontruck.states;

import com.github.ontruck.driver.IDriver;

public abstract class StateFilter implements IDriver {
	private MopedState state;

	public void setState(MopedState state) {
		this.state = state;
	}

	public MopedState getState() {
		return state;
	}
}
