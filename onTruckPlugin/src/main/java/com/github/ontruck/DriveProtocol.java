package com.github.ontruck;

import com.github.moped.jcan.CAN;

import java.io.IOException;

public class DriveProtocol {

	private static final byte STEER_OP_CODE = 0x53;
	private static final byte POWER_OP_CODE = 0x50;
	private static final byte BRAKE_OP_CODE = 0x42;

	private MopedState state = MopedState.Manual;

	private static final byte[] usefulPowerValues = new byte[] {
			0, 7, 11, 15, 19, 23, 27, 37, 41, 45, 49, 53, 57, 73, 77, 81, 85, 89, 93, 97, 100
	};

	private CAN can;
	private byte lastPowerValue = 0;

	public DriveProtocol() throws IOException {
		String canInterface = System.getenv("CAN_INTERFACE");
		try {
			can = new CAN(canInterface);
		} catch(IOException e) {
			System.err.printf("Failed to connect to CAN interface [%s]%n", canInterface);
			throw e;
		}
	}

	public void emergencyStop() {
		if(
			MopedState.Manual                == state ||
			MopedState.CruiseControl         == state ||
			MopedState.AdaptiveCruiseControl == state
		) {
			brake((byte)0xFF);
		}
	}

	// Process TCP Event
	public void processEvent(String message){
		System.out.println(message);
		// ToDo
	}

	// Process UDP Event
	public void processEvent(byte type, byte payload, byte stateGroup) {
		switch(type) {
			case POWER_OP_CODE:
				state = MopedState.Manual;
				power(payload);
				break;

			case STEER_OP_CODE:
				if(MopedState.Platooning == state) {
					state = MopedState.Manual;
				}
				steer(payload);
				break;

			case BRAKE_OP_CODE:
				state = MopedState.Manual;
				brake(payload);
				break;
			default:
				// TODO: Error(?)
		}
	}

	private static byte maxMinPayload(byte payload, byte min, byte max) {
		// The CAN accepts control values within a certain range, thus we convert the payload
		int tmp = payload;
		if(tmp >= 0) {
			tmp *= max;
			tmp /= 127;
		} else {
			tmp *= min;
			tmp /= 128;
		}
		return (byte)tmp;
	}

	private static int umaxPayload(byte payload, byte max) {
		// The CAN accepts control values within a certain range, thus we convert the payload
		int tmp = Byte.toUnsignedInt(payload);
		tmp *= max;
		tmp /= 255;
		return tmp;
	}


	private void power(byte payload) {
		byte powerLevel = maxMinPayload(payload, (byte)100, (byte)100); // Over 9000?
		// If different sign
		if(powerLevel * lastPowerValue < 0) {
			try {
				// If the user wants to reverse engine direction, then we need to tell it to stop first.
				can.sendMotorValue((byte)0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		powerLevel = getAppropriatePowerLevel(powerLevel);

		try {
			can.sendMotorValue(powerLevel);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		lastPowerValue = powerLevel;
	}

	private void steer(byte payload) {
		try {
			can.sendSteerValue(maxMinPayload(payload, (byte)100, (byte)100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void brake(byte payload) {
		int brakeValue = umaxPayload(payload, (byte)100);

		if(lastPowerValue > 0) {
			// Engine will break if engine is set to the opposite direction it was driving.
			// Therefore, if we are driving forward, we need to set the engine in reverse.
			brakeValue = -brakeValue;
		} else if(lastPowerValue < 0){
			// The break value is positive, which is what we want.
		} else {
			// TODO: needs testing
		}

		try {
			can.sendMotorValue((byte)brakeValue);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private byte getAppropriatePowerLevel(byte powerLevel) {
		if(powerLevel >= 0) {
			for (byte n : usefulPowerValues) {
				if(n >= powerLevel) {
					return n;
				}
			}
			return usefulPowerValues[usefulPowerValues.length-1];
		} else {
			for (byte n : usefulPowerValues) {
				if(n >= -powerLevel) {
					return (byte)-n;
				}
			}
			return (byte)-usefulPowerValues[usefulPowerValues.length-1];
		}
	}

	private enum MopedState {
		Manual,
		CruiseControl,
		AdaptiveCruiseControl,
		Platooning,
	}
}
