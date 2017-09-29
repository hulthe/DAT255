package com.github.ontruck;

import com.github.moped.jcan.CAN;

import java.io.IOException;

public class DriveProtocol {

	private static final byte STEER_OP_CODE = 0x53;
	private static final byte POWER_OP_CODE = 0x50;
	private static final byte BRAKE_OP_CODE = 0x42;

	private CAN can;

	public DriveProtocol() throws IOException {
		String canInterface = System.getenv("CAN_INTERFACE");
		try {
			can = new CAN(canInterface);
		} catch(IOException e) {
			System.err.printf("Failed to connect to CAN interface [%s]%n", canInterface);
			throw e;
		}
	}

	public void processEvent(byte type, byte payload) {
		switch(type) {
			case POWER_OP_CODE:
				power(payload);
				break;
			case STEER_OP_CODE:
				steer(payload);
				break;
			case BRAKE_OP_CODE:
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

	private void power(byte payload) {
		try {
			can.sendMotorValue(maxMinPayload(payload, (byte)-100, (byte)100));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void steer(byte payload) {
		try {
			can.sendSteerValue(maxMinPayload(payload, (byte)-10, (byte)10));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void brake(byte payload) {
		/*try {
			can.sendBrakeValue(umaxPayload(payload, (byte)0, (byte)100));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
}
