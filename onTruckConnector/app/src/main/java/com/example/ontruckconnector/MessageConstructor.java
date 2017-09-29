package com.example.ontruckconnector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MessageConstructor {

    public void onMove(char type, byte payload) {
        byte[] message = constructMessage(type, payload);
    }

    private byte[] constructMessage(char type, byte payload) {
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
