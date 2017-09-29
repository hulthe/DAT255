package com.example.ontruckconnector;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;



public class TCPChecker extends AsyncTask<String, Void, TCPChecker> {


	private final String IP_ADDRESS; 	//the IP_ADDRESS is set in the constructor
	private final int PORT;				//the port is set in the constructor
	private final int COOLDOWN = 1000;  //this is in ms and is the delay between pings
	Socket socket = null;
	String sentData = "connectiontest"; //"bullshit" data sent to the server
	String recievedData = "";			//"bullshit" data sent from the server
	boolean connection = false;			//if there is a TCP connection to the server

	@Override
	protected TCPChecker doInBackground(String... strings) {
		run();
		return null;
	}

	//public TCPChecker(String ipAddress, int port, MainActivity mainActivity) {
	public TCPChecker(String ipAddress, int port) {
		IP_ADDRESS = ipAddress;
		PORT = port;
	}

	public void run() {
		//If we get a connection related exception -> try connecting again
		while (true) {

			try {
				//Alternate between the check() code(attempting to connect) and sleeping
				while (true) {
					check();
					sleep(COOLDOWN);
				}

				//Exception handling
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {

				//Close socket since it wont have been if an exception was thrown
				if (socket != null && !socket.isClosed()) {
					try {
						socket.close();
						socket = null;


						//Exception handling
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void check() {
		try {
			//Step 0, create socket and initialize the input stream and the output stream
			socket = new Socket();
			socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), COOLDOWN);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			//Step 1, send length
			Log.i("TCP", "Length" + sentData.length());
			output.writeInt(sentData.length());

			//Step 2, send data
			Log.i("TCP", "Writing.......");
			output.writeBytes(sentData); // UTF is a string encoding

			//Step 3, read length
			int nb = input.readInt();
			byte[] digit = new byte[nb];

			//Step 4, read data
			for (int i = 0; i < nb; i++){
				digit[i] = input.readByte();}
			recievedData = new String(digit);
			Log.i("TCP", "Received: " + recievedData);

			//Error handling
		} catch (UnknownHostException e) {
			Log.i("TCP", "Sock:" + e.getMessage());
		} catch (EOFException e) {
			Log.i("TCP", "EOF:" + e.getMessage());
		} catch (IOException e) {
			Log.i("TCP", "IO:" + e.getMessage());
		} catch (NullPointerException e){
			Log.i("TCP", "NullPointer:" + e.getMessage());
		} catch (Exception e){
			Log.i("TCP", "Exception: " + e.getMessage());
		} finally{

			//Closes socket
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					Log.i("TCP", "IO:" + e.getMessage());
				}

			//If statement regarding the received data from the server
			if (!recievedData.equals("")) {
				Log.i("TCP", "Connection successful");
				connection = true;
			} else {
				Log.i("TCP", "Connection unsuccessful");
				connection = false;
			}

			//This gets called after each "tick", calls onProgressUpdate()
			this.publishProgress();

			//Nulls the received data between each "tick"
			recievedData = "";
		}
	}

	//Gives the connection boolean to the Holder -> possibly toggle the connection text
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		ConnectionTextHolder.getInstance().setConnection(connection);
	}
}
