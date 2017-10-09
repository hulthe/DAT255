package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class MainActivity extends AppCompatActivity {

	//All UI-elements. Initializes in onCreate()
	private TextView connectionText;
	private JoystickPosition joystickPosition = new JoystickPosition();
	private MessageConstructor messageConstructor = new MessageConstructor();
	private JoystickView joystickView;
	private UDPSender udpSender;
	private ToggleButton accToggle;
	private EditText ipInput;
	private Thread UDPThread;
	private TCPConnection tcpConnection;
	private String oldIP;

	private static final int PORT = 8721;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		//todo olIP = getIP form last session


		//Initializes the UI-elements
		connectionText = (TextView)findViewById(R.id.connectionText);
		joystickView = (JoystickView)findViewById(R.id.joystick);
		accToggle = (ToggleButton)findViewById(R.id.accToggle);
		ipInput = (EditText)findViewById(R.id.ipInput);

		//todo this comnment
		updateTCP();
		updateUDP();

		//Initializes a Holder-object to avoid double coupling between MainActivity and TCPConnection
		ConnectionTextHolder connectionTextHolder = ConnectionTextHolder.getInstance();
		connectionTextHolder.setTextView(connectionText);

		//Sets a listener which listens when the joystick is moved to change the X and Y values in JoystickPosition
		joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
			@Override
			public void onMove(int angle, int strength) {
				joystickPosition.onUpdate(angle, strength);
			}
		});

		accToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (accToggle.isChecked()) {
					tcpConnection.send("Hur mar du gurgyuyguyu");
				}
			}
		});


		UDPThread = initilizeUDPThread();
		UDPThread.start();
		tcpConnection.execute();


		ipInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void afterTextChanged(Editable editable) {
				//Each time the IPinput changes, if it is a correct IP:
				//UDP and TCP thread stops and restarts with new IP
				if(isValidIP(ipInput.getText().toString())){

					//Stop old threads
					tcpConnection.cancel(true);
					UDPThread.interrupt();

					//Create new threads with correct IP
					updateUDP();
					updateTCP();
					
					//Runs the new Threads
					UDPThread = initilizeUDPThread();
					UDPThread.start();
					tcpConnection.execute();
				}
			}
		});


    }

    private Thread initilizeUDPThread(){
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
		return thread;
	}

	private boolean isValidIP(String input){
		Pattern p = Pattern.compile(
				"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)" +
				"{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
		return p.matcher(input).matches();
	}


	private void updateTCP(){
		String newIP = oldIP;
		if(isValidIP(ipInput.getText().toString())){
			newIP = ipInput.getText().toString();
		}
		if(!(newIP == null) || !newIP.equals("")){
			tcpConnection = new TCPConnection(newIP, PORT);
		}
	}

    private void updateUDP(){
		String newIP = oldIP;

		if(isValidIP(ipInput.getText().toString())){
			newIP = ipInput.getText().toString();
		}

		if(!(newIP == null) || !newIP.equals("")){
			udpSender = createUDPSender(newIP, PORT);
		}
	}

    private UDPSender createUDPSender(String ip, int port){
		//Creates the UDPSender object with an IP address and port number
		UDPSender udpSender = null;
		try{
			udpSender = new UDPSender(ip, port);}
		catch(SocketException e){
			e.printStackTrace();
		} catch(UnknownHostException e){
			Log.e(this.getClass().getName(), "Unable to create InetAddress");
			e.printStackTrace();
		}
		return udpSender;
	}


}
