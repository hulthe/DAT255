package com.example.ontruckconnector;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * The class that handles the UDP communication.
 */
public class UDPSender {

    /**
     * The port the data should be sent on to.
     */
    private final int SERVER_PORT;

    /**
     * The address the data should be sent to.
     */
    private final String ADDRESS;

    /**
     * The socket the data should be sent across.
     */
    private final DatagramSocket socket;

    /**
     * The packet with data that should be sent.
     */
    private final DatagramPacket packet;


    /**
     * Creates a {@link UDPSender} with a given address and given port.
     *
     * @param address the given address that the data should be sent to.
     * @param port    the given port that the data should be sent to.
     * @throws SocketException      thrown if the creation of the {@link DatagramSocket} fails.
     * @throws UnknownHostException thrown if the given address wasn't a known host.
     */
    public UDPSender(String address, int port) throws SocketException, UnknownHostException {
        // All this information can be reused each time a message is sent
        // and is therefor stored in the UDPSender class
        ADDRESS = address;
        SERVER_PORT = port;
        socket = new DatagramSocket();
        InetAddress iNetAddress = InetAddress.getByName(ADDRESS);
        packet = new DatagramPacket(new byte[7], 7, iNetAddress, SERVER_PORT);
    }

    /**
     * Sends the given data to the set address and port.
     *
     * @param input the data that should be sent.
     */
    void sendMessage(byte[] input) {
        // First the packet is locked to avoid thread issues
        // otherwise it is possible for multiple threads to attempt to access the same packet object
        synchronized (packet) {
            packet.setData(input);
            try {
                // Then the socket is held to avoid thread issues,
                // otherwise it is possible for multiple threads to attempt to access the same socket object
                synchronized (socket) {
                    socket.send(packet);
                }
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Unable to send message");
                e.printStackTrace();
            }
        }
    }

}