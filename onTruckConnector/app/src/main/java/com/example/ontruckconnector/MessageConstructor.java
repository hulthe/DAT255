package com.example.ontruckconnector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessageConstructor {




    public byte[] coordinatePowerToMessage(int y){
        return new byte[]{0x01, 0x50, 0x7F, 0x07, (byte)0x91, 0x04};
    }
    public byte[] coordinateSteeringToMessage(int x){
        return new byte[]{0x01, 0x42, 0x3F, 0x03, 0x5D, 0x04};
    }

    /**
     *
     * @param type is the type of movement
     * @param payload is the value of that movement
     */
    public void onMove(char type, byte payload) {
        byte[] message = constructMessage(type, payload);
    }

    /**
     * This method constructs a byte array message with length 6
     * @param type is the type of movement
     * @param payload is the value of that movement
     */
    byte[] constructMessage(char type, byte payload) {
        byte[] message = new byte[6];
        message[0] = 1;
        message[1] = Byte.decode(String.valueOf(type));
        message[2] = payload;
        byte[] checksum = createChecksum(Byte.decode(String.valueOf(type)), payload);
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
