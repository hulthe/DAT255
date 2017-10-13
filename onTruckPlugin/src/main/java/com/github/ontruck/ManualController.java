package com.github.ontruck;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static com.github.ontruck.Driver.BRAKE_OP_CODE;
import static com.github.ontruck.Driver.POWER_OP_CODE;
import static com.github.ontruck.Driver.STEER_OP_CODE;

public class ManualController {

	private final IDriver driver;

	public ManualController(IDriver driver) {
		this.driver = driver;
	}

	// Process UDP Event
	public void processEvent(byte type, byte payload, byte stateGroup) {
		switch(type) {
			case POWER_OP_CODE:
				driver.power(payload);
				break;

			case STEER_OP_CODE:
				driver.steer(payload);
				break;

			case BRAKE_OP_CODE:
				driver.brake(payload);
				break;
			default:
				throw new NotImplementedException();
		}
	}
}
