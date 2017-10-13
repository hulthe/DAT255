package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;

public abstract class StateFilter implements IDriver {
	private MopedState state;

	public void setState(MopedState state) {
		this.state = state;
	}

	public MopedState getState() {
		return state;
	}
}
