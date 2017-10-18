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

	public Driver(CAN can) throws IOException {
		this.can = can;

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

	private static int maxPayload(byte payload, byte max) {
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
			if(can != null){
			can.sendMotorValue(powerLevel);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		lastPowerValue = powerLevel;
	}

	private void rawPower(byte powerLevel){
		try {
			if(can != null){
				can.sendMotorValue(powerLevel);
			}
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
		int brakeValue = maxPayload(payload, (byte)100);

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

	//This method calls the rawPower(byte) method to be able to accelerate the car
	//This works by reading the last byte sent to the CAN and then sending a byte one step higher
	@Override
	public void increaseSpeed() {
		byte newPowerValue = lastPowerValue;

		//If the car already is at full speed then don't accelerate (keep at 100)
		if (lastPowerValue >= 100) {
			newPowerValue = 100;
		} else {

			//Loop through the list of useful power values
			for (byte usefulPowerValue : usefulPowerValues) {

				//If the next useful power value is reached then stop the loop and use that value
				if (lastPowerValue < usefulPowerValue) {
					newPowerValue = usefulPowerValue;
					break;
				} else if (lastPowerValue < usefulPowerValue * -1) {
					newPowerValue = (byte) (usefulPowerValue * -1);
					break;
				}
			}
		}
		//Added again for testing purposes
		lastPowerValue = newPowerValue;

		//Send to CAN!
		rawPower(newPowerValue);
	}

	//This method calls the rawPower(byte) method to be able to decelerate the car
	//This works by reading the last byte sent to the CAN and then sending a byte one step lower
	@Override
	public void decreaseSpeed() {
		byte newPowerValue = lastPowerValue;

		//If the car already is at full reverse speed then don't accelerate (keep at -100)
		if (lastPowerValue <= -100) {
			newPowerValue = -100;
		} else {

			//Loop through the list of useful power values
			for (byte usefulPowerValue : usefulPowerValues) {

				//If the next useful power value is reached then stop the loop and use that value
				if (lastPowerValue > usefulPowerValue) {
					newPowerValue = usefulPowerValue;
					continue;
				} else if (lastPowerValue > usefulPowerValue * -1) {
					newPowerValue = (byte) (usefulPowerValue * -1);
					break;
				}
			}
		}

		//Added again for testing purposes
		lastPowerValue = newPowerValue;

		//Send to CAN!
		rawPower(newPowerValue);
	}

	// This is for testing purposes
	public byte getLastPowerValue(){
		return lastPowerValue;
	}

	//This is for testing purposes
	public void setLastPowerValue(byte lastPowerValue){
		this.lastPowerValue = lastPowerValue;
	}

	//This is for testing purposes
	public byte[] getUsefulPowerValues(){
		return usefulPowerValues;
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

}
