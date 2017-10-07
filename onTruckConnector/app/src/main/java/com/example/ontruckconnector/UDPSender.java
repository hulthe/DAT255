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
		//All this information can be reused each time a message is sent
		// and is therefor stored in the UDPSender class
		ADDRESS = address;
		SERVER_PORT = port;
		socket = new DatagramSocket();
		InetAddress iNetAddress = InetAddress.getByName(ADDRESS);
		packet = new DatagramPacket(new byte[7], 7, iNetAddress, SERVER_PORT);
	}

    void sendMessage(byte[] input){
		//First the packet is held to avoid thread issues
		// otherwise it is possible for multiple threads to attempt to access the same packet object
		synchronized (packet){
			packet.setData(input);
        try{
			//Then the socket is held to avoid thread issues,
			// otherwise it is possible for multiple threads to attempt to access the same socket object
            synchronized (socket){
				socket.send(packet);}
        }catch(IOException e){
            Log.e(this.getClass().getName(), "Unable to send message");
            e.printStackTrace();
        	}
		}
    }

}