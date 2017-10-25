package com.github.ontruck.controller;

import com.github.ontruck.driver.IDriver;
import com.github.ontruck.network.UDPConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ManualController {

	private final IDriver driver;

	public ManualController(IDriver driver) {
		this.driver = driver;
	}

	// Process UDP Event
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
