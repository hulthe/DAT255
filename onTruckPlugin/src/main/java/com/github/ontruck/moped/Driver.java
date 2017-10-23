package com.github.ontruck.moped;

import com.github.moped.jcan.CAN;
import com.github.ontruck.driver.IDriver;
import com.github.ontruck.states.MopedState;

import java.io.IOException;

public class Driver implements IDriver {


	protected static final byte MAX_POWER_VALUE = 100;

	private static final byte[] usefulPowerValues = new byte[] {
			0, 7, 11, 15, 19, 23, 27, 37, 41, 45, 49, 53, 57, 73, 77, 81, 85, 89, 93, 97, 100
	};

	private MopedState state = MopedState.Manual;
	private CAN can;
	private byte lastPowerValue = 0;
	private byte lastSteerValue = 0;

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
		byte powerLevel = maxMinPayload(payload, MAX_POWER_VALUE, MAX_POWER_VALUE); // Over 9000?
		// If different sign
		if(powerLevel * lastPowerValue < 0) {
			try {
				// If the user wants to reverse engine direction, then we need to tell it to stop first.
				can.sendMotorValue((byte)0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		rawPower(getAppropriatePowerLevel(powerLevel));
	}

	private void rawPower(byte powerLevel){
		if(powerLevel != lastPowerValue) {
			try {
				if(can != null){
					can.sendMotorValue(powerLevel);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			lastPowerValue = powerLevel;
		}
	}

	public void steer(byte payload) {
		byte steerValue = maxMinPayload(payload, (byte)100, (byte)100);
		if(steerValue != lastSteerValue) {
			try {
				can.sendSteerValue(steerValue);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			lastSteerValue = steerValue;
		}
	}

	public void brake(byte payload) {
		int brakeValue = maxPayload(payload, MAX_POWER_VALUE);

		if(lastPowerValue > 0) {
			// Engine will break if engine is set to the opposite direction it was driving.
			// Therefore, if we are driving forward, we need to set the engine in reverse.
			brakeValue = -brakeValue;
		} else if(lastPowerValue < 0){
			// The break value is positive, which is what we want.
		} else {
			// TODO: needs testing
		}

		// TODO Don't spam can with brake commands

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
		if (lastPowerValue >= MAX_POWER_VALUE) {
			newPowerValue = MAX_POWER_VALUE;
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


		//Send to CAN!
		rawPower(newPowerValue);
	}

	//This method calls the rawPower(byte) method to be able to decelerate the car
	//This works by reading the last byte sent to the CAN and then sending a byte one step lower
	@Override
	public void decreaseSpeed() {
		byte newPowerValue = lastPowerValue;

		//If the car already is at full reverse speed then don't accelerate (keep at -100)
		if (lastPowerValue <= -MAX_POWER_VALUE) {
			newPowerValue = -MAX_POWER_VALUE;
		} else {

			//Loop through the list of useful power values
			for (int i = usefulPowerValues.length-1; i > 0; i--) {

				byte usefulPowerValue = usefulPowerValues[i];

				//If the next useful power value is reached then stop the loop and use that value
				if (lastPowerValue > usefulPowerValue) {
					newPowerValue = usefulPowerValue;
					break;
				} else if (lastPowerValue < -usefulPowerValue) {
					newPowerValue = (byte)-usefulPowerValue;
					break;
				}
			}
		}

		//Send to CAN!
		rawPower(newPowerValue);
	}

	@Override
	public byte getLastPowerValue(){
		return lastPowerValue;
	}

	//This is for testing purposes
	public void setLastPowerValue(byte lastPowerValue){
		this.lastPowerValue = lastPowerValue;
	}

	//This is for testing purposes
	public byte[] getUsefulPowerValues(){
		return usefulPowerValues.clone();
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
