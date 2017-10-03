package com.example.ontruckconnector;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSender {

    private final int SERVER_PORT;
    private final String ADDRESS;
	private final DatagramSocket socket;
	private final DatagramPacket packet;


    public UDPSender(String address, int port) throws SocketException, UnknownHostException {
		ADDRESS = address;
		SERVER_PORT = port;
		socket = new DatagramSocket();
		InetAddress iNetAddress = InetAddress.getByName(ADDRESS);
		packet = new DatagramPacket(new byte[6], 6, iNetAddress, SERVER_PORT);
	}

    void sendMessage(byte[] input){
		synchronized (packet){
			packet.setData(input);
        try{
            synchronized (socket){
				socket.send(packet);}
        }catch(IOException e){
            Log.e(this.getClass().getName(), "Unable to send message");
            e.printStackTrace();
        	}
		}
    }

}