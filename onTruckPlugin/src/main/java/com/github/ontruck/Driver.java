package com.github.ontruck;

import com.github.moped.jcan.CAN;

import java.io.IOException;

public class Driver implements IDriver {

	protected static final byte STEER_OP_CODE = 0x53;
	protected static final byte POWER_OP_CODE = 0x50;
	protected static final byte BRAKE_OP_CODE = 0x42;

	private static final byte[] usefulPowerValues = new byte[] {
			0, 7, 11, 15, 19, 23, 27, 37, 41, 45, 49, 53, 57, 73, 77, 81, 85, 89, 93, 97, 100
	};

	private MopedState state = MopedState.Manual;
	private CAN can;
	private byte lastPowerValue = 0;

	public Driver() throws IOException {
		String canInterface = System.getenv("CAN_INTERFACE");
		try {
			can = new CAN(canInterface);
		} catch(IOException e) {
			System.err.printf("Failed to connect to CAN interface [%s]%n", canInterface);
			throw e;
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


	public void power(byte payload) {
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

	public void steer(byte payload) {
		try {
			can.sendSteerValue(maxMinPayload(payload, (byte)100, (byte)100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void brake(byte payload) {
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

	public byte getLastPowerValue(){
		return lastPowerValue;
	}

}
