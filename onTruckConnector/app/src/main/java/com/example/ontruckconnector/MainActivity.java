package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.net.SocketException;
import java.net.UnknownHostException;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class MainActivity extends AppCompatActivity {

	//All UI-elements. Initializes in onCreate()
	private TextView connectionText;
	private JoystickPosition joystickPosition = new JoystickPosition();
	private MessageConstructor messageConstructor = new MessageConstructor();
	private JoystickView joystickView;
	private UDPSender udpSender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Creates the UDPSender object with an IP address and port number
		try{
			udpSender = new UDPSender("192.168.43.75", 8721);}
		catch(SocketException e){
			e.printStackTrace();
		} catch(UnknownHostException e){
			Log.e(this.getClass().getName(), "Unable to create InetAddress");
			e.printStackTrace();
		}

		//Initializes the UI-elements
		connectionText = (TextView)findViewById(R.id.connectionText);
		joystickView = (JoystickView)findViewById(R.id.joystick);

		//Initializes a Holder-object to avoid double coupling between MainActivity and TCPChecker
		ConnectionTextHolder connectionTextHolder = ConnectionTextHolder.getInstance();
		connectionTextHolder.setTextView(connectionText);

		//Sets a listener which listens when the joystick is moved to change the X and Y values in JoystickPosition
		joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
			@Override
			public void onMove(int angle, int strength) {
				joystickPosition.onUpdate(angle, strength);
			}
		});

		//This thread runs the udp sending code
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try{
						//TODO: Remove this later
						synchronized (this) {
							// We want constant UDP-sendings when the UDP-server library has been updated
							//Also: Ugly code?
							wait(100);
						}
					}catch(InterruptedException e){
						e.printStackTrace();
					}
						//Each tick the MessageConstructor creates a protocol message using the
						//joysticks X and Y values and sends it using the UDPSender
						udpSender.sendMessage(messageConstructor.coordinateSteeringToMessage(joystickPosition.getX()));
						udpSender.sendMessage(messageConstructor.coordinatePowerToMessage(joystickPosition.getY()));
				}
			}
		});

		thread.start();





		//Excecutes the TCP-connection
        new TCPChecker("192.168.43.150", 8721).execute();
    }


}
