package com.github.ontruck.states;

import com.github.ontruck.driver.IDriver;

/**
 * Filters data through a filer based on the current state in the filter.
 * The state is a MOPED state and can be both set and received.
 */
public abstract class StateFilter implements IDriver {
	private MopedState state;

	public void setState(MopedState state) {
		this.state = state;
	}

	public MopedState getState() {
		return state;
	}
}
