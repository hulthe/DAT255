package com.example.ontruckconnector;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSender {

    private final int SERVER_PORT = 8722;
    private final String ADDRESS = "192.168.43.150";

    private static UDPSender instance;


    private UDPSender(){}

    static UDPSender getInstance(){
        if(instance == null){
            instance = new UDPSender();}
        return instance;
    }

    void sendMessage(byte[] input){
        try{
            DatagramSocket s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(ADDRESS);
            DatagramPacket p = new DatagramPacket(input, input.length,local,SERVER_PORT);
            s.send(p);
        }
        catch(SocketException e){
            Log.e(this.getClass().getName(), "Unable to create DatagramSocket");
            e.printStackTrace();
        }catch(UnknownHostException e){
            Log.e(this.getClass().getName(), "Unable to create InetAddress");
            e.printStackTrace();
        }catch(IOException e){
            Log.e(this.getClass().getName(), "Unable to send message");
            e.printStackTrace();
        }
    }
}