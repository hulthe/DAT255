package com.github.ontruck.controller;

import com.github.ontruck.driver.IDriver;
import com.github.ontruck.network.UDPConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Passes commands from a data source (e.g. {@link UDPConnection}) to an {@link IDriver}.
 */
public class ManualController {

	private final IDriver driver;

	public ManualController(IDriver driver) {
		this.driver = driver;
	}

	/**
	 * Process driver command. It chooses how to send along the payload to the driver.
 	 */
	public void processEvent(byte type, byte payload, byte stateGroup) {
		switch(type) {
			case UDPConnection.POWER_OP_CODE:
				driver.power(payload);
				break;

			case UDPConnection.STEER_OP_CODE:
				driver.steer(payload);
				break;

			case UDPConnection.BRAKE_OP_CODE:
				driver.brake(payload);
				break;
			default:
				throw new NotImplementedException();
		}
	}
}
