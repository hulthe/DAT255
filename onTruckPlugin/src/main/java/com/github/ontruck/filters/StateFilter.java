package com.github.ontruck.filters;

import com.github.ontruck.IDriver;
import com.github.ontruck.MopedState;

abstract class StateFilter implements IDriver {
	private MopedState state;

	void setState(MopedState state) {
		this.state = state;
	}

	MopedState getState() {
		return state;
	}
}
