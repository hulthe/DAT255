package com.example.ontruckconnector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//This class is used to create protocol messages used over UDP
public class MessageConstructor {



	//This method takes in the power(y) and returns a correct protocol message
	public byte[] coordinatePowerToMessage(int y){
		char returnValue;
		if(y == 0){
			//The char for breaking
			returnValue = (char)0x42;
		}else{
			//The char for POWER
			returnValue = (char)0x50;
		}
		return constructMessage(returnValue, (byte)y);
	}

	//This method takes in the sterring power(x) and returns a correct protocol message
	public byte[] coordinateSteeringToMessage(int x){
		//The char for steering
        return constructMessage((char)0x53, (byte) x);
    }

    /**
     * This method constructs a byte array message with length 6
     * @param type is the type of movement
     * @param payload is the value of that movement
     */
    byte[] constructMessage(char type, byte payload) {
        byte[] message = new byte[6];
        message[0] = 1;
        message[1] = (byte)type;
        message[2] = payload;
        byte[] checksum = createChecksum((byte)type, payload);
        message[3] = checksum[0];
        message[4] = checksum[1];
        message[5] = 4;
        return message;
    }

    /**
     * This method returns a MD5 hash of type and payload
     * @param type is the type of movement
     * @param payload is the value of that movement
     */
    private byte[] createChecksum(byte type, byte payload) {
        byte[] checksum = new byte[2];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] input = new byte[2];
            input[0] = type;
            input[1] = payload;
            md5.update(input);
            checksum = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return checksum;
    }
}
