package com.github.ontruck.filters;

import com.github.ontruck.MopedState;

public class StateFilter {
	private MopedState state;

	public void setState(MopedState state) {
		this.state = state;
	}

	public MopedState getState() {
		return state;
	}
}
