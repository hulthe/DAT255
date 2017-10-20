package com.example.ontruckconnector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * This class is used to create protocol messages used over UDP.
 */
public class MessageConstructor {

	/**
	 * This method takes in the power, y, and returns a correct protocol message.
	 * @param y, power
	 * @return Correct Power Protocol Message
	 */
	public byte[] coordinatePowerToMessage(int y){
		char returnValue;
		if(y == 0){
			//The char for breaking
			returnValue = (char)0x42;
		}else{
			//The char for POWER
			returnValue = (char)0x50;
		}
		return constructMessage(returnValue, (byte)y, (byte)0);
	}

	/**
	 * This method takes in the steering power, x, and returns a correct protocol message
	 * @param x, steering
	 * @return Correct Steering Protocol Message
	 */
	public byte[] coordinateSteeringToMessage(int x){
		//The char for steering
		return constructMessage((char)0x53, (byte)x, (byte)0);
	}

	/**
	 * This method constructs a byte array message with length 6
	 * @param type is the type of movement
	 * @param payload is the value of that movement
	 */
	byte[] constructMessage(char type, byte payload, byte stateGroup) {
		byte[] message = new byte[7];
		message[0] = 1;
		message[1] = (byte)type;
		message[2] = payload;
		byte[] checksum = createChecksum((byte)type, payload, stateGroup);
		message[3] = stateGroup;
		message[4] = checksum[0];
		message[5] = checksum[1];
		message[6] = 4;
		return message;
	}

	/**
	 * This method returns a MD5 hash of type and payload
	 * @param type is the type of movement
	 * @param payload is the value of that movement
	 */
	private byte[] createChecksum(byte type, byte payload, byte stateGroup) {
		byte[] checksum = new byte[2];
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] input = new byte[3];
			input[0] = type;
			input[1] = payload;
			input[2] = stateGroup;
			md5.update(input);
			checksum = md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return checksum;
	}
}
