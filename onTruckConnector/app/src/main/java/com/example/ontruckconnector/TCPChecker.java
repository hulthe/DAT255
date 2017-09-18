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


	private final String IP_ADDRESS;
	private final int PORT;
	private final int COOLDOWN = 1000; //this is in ms
	Socket socket = null;
	String sentData = "connectiontest";
	String recievedData = "";
	boolean connection = false;
	MainActivity mainActivity;

	@Override
	protected TCPChecker doInBackground(String... strings) {
		run();
		return null;
	}

	public TCPChecker(String ipAddress, int port, MainActivity mainActivity) {
		IP_ADDRESS = ipAddress;
		PORT = port;
		this.mainActivity = mainActivity;
	}

	public void run() {
		while (true) {
			try {
				while (true) {
					check();
					sleep(COOLDOWN);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (socket != null && !socket.isClosed()) {
					try {
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void check() {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), COOLDOWN);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			//Step 1 send length
			Log.i("TCP", "Length" + sentData.length());
			output.writeInt(sentData.length());
			//Step 2 send length
			Log.i("TCP", "Writing.......");
			output.writeBytes(sentData); // UTF is a string encoding

			//Step 1 read length
			int nb = input.readInt();
			byte[] digit = new byte[nb];
			//Step 2 read byte
			for (int i = 0; i < nb; i++)
				digit[i] = input.readByte();

			recievedData = new String(digit);
			Log.i("TCP", "Received: " + recievedData);
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
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {/*close failed*/}
			if (!recievedData.equals("")) {
				Log.i("TCP", "Connection successful");
				connection = true;
			} else {
				Log.i("TCP", "Connection unsuccessful");
				connection = false;
			}
			this.publishProgress();
			recievedData = "";
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		ConnectionTextHolder.getInstance().setConnection(connection);
	}
}
