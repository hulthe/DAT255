package com.example.ontruckconnector;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

/**
 * Created by nan on 9/12/17.
 */

public class TCPChecker extends AsyncTask<String, Void, TCPChecker> {

    private final String IP_ADDRESS;
    private final int PORT;
    Socket socket = null;

    public TCPChecker(String ipAddress, int port){
        IP_ADDRESS = ipAddress;
        PORT = port;
    }

    public void run() {
        try{
            while(true){
                check();
                sleep(100);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            if(socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void check() {
        try {
            String data = "Hello, How are you?";
            socket = new Socket(IP_ADDRESS, PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //Step 1 send length
            System.out.println("Length" + data.length());
            output.writeInt(data.length());
            //Step 2 send length
            System.out.println("Writing.......");
            output.writeBytes(data); // UTF is a string encoding

            //Step 1 read length
            int nb = input.readInt();
            byte[] digit = new byte[nb];
            //Step 2 read byte
            for (int i = 0; i < nb; i++)
                digit[i] = input.readByte();

            String st = new String(digit);
            System.out.println("Received: " + st);
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {/*close failed*/}
        }
    }
}
