package com.example.ontruckconnector;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSender {

    private final int SERVER_PORT = 8721;
    private final String ADDRESS = "192.168.43.150";

    private static UDPSender instance;


}
