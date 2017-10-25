package com.example.ontruckconnector;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import io.github.controlwear.virtual.joystick.android.JoystickView;


/**
 * The starting point of the application.
 */
public class MainActivity extends AppCompatActivity {

	//All UI-elements. Initializes in onCreate()
	private TextView connectionText;
	private JoystickPosition joystickPosition = new JoystickPosition();
	private MessageConstructor messageConstructor = new MessageConstructor();
	private JoystickView joystickView;
	private UDPSender udpSender;
	private ToggleButton accToggle;
	private ImageView borderImage;
	private EditText ipInput;
	private Thread UDPThread;
	private TCPConnection tcpConnection;
	private ImageView connectionImage;
	private String oldIP;

	private static final int PORT = 8721;


	/**
	 * Runs when the activity is being created (see android activity life cycles for more
	 * information about this.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initializes the UI-elements
		connectionText = (TextView) findViewById(R.id.connectionText);
		joystickView = (JoystickView) findViewById(R.id.joystick);
		accToggle = (ToggleButton) findViewById(R.id.accToggle);
		borderImage = (ImageView) findViewById(R.id.borderImage);
		ipInput = (EditText) findViewById(R.id.ipInput);
		connectionImage = (ImageView) findViewById(R.id.connectionImage);

		//Initializes a Holder-object to avoid double coupling between MainActivity and TCPConnection
		GUIHolder guiHolder = GUIHolder.getInstance();
		guiHolder.setTextView(connectionText);
		guiHolder.setToggleButton(accToggle);
		guiHolder.setImageView(connectionImage);
		guiHolder.setBorderImage(borderImage);

		//Creates the TCP/UDP clients
		updateTCP();
		updateUDP();

		//Sets a listener which listens when the joystick is moved to change the X and Y values in JoystickPosition
		joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
			@Override
			public void onMove(int angle, int strength) {
				joystickPosition.onUpdate(angle, strength);
			}
		});

		// Specifies that a ACC state message should be sent over TCP when the acc toggle button is
		// pressed.
		accToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// Send new eventGroup
				tcpConnection.send(String.format(
						"{\"type\":\"event_group\", \"value\":%s}",
						0 /*TODO: Add eventGroup variable*/
				));

				// Set new state
				tcpConnection.send(String.format(
						"{\"type\":\"state\", \"value\":\"%s\"}",
						accToggle.isChecked() ? "ACC" : "M"
				));
			}
		});

		// Reset the toggle button before first start
		GUIHolder.getInstance().resetToggleButton();

		// Tells the colors of the button to change upon being toggles either way.
		accToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				System.out.println("accToggle pressed");
				accToggle.setText("CHANGED");
				if (GUIHolder.getInstance().getConnection()) {
					//if(true){
					//Todo: remove this comment ^
					accToggle.setBackgroundColor(Color.parseColor("#CCCCCC"));
					accToggle.setTextColor(Color.parseColor("#222222"));
					if (b) {
						GUIHolder.getInstance().activateToggleButton();
					} else {
						GUIHolder.getInstance().deacativateToggleButton();
					}
				} else {
					GUIHolder.getInstance().resetToggleButton();
				}
			}
		});

		// Start both UDPThread and tcpConnection.
		UDPThread = initializeUDPThread();
		UDPThread.start();
		tcpConnection.execute();

		// Specify what should happen when the text in the ip address text box is changed.
		// It turns of the ongoing tcp connection and tries to start a new one with the entered
		// ip-address. It checks if the ip-address entered is a valid one and if it is
		ipInput.addTextChangedListener(new TextWatcher() {
			// We need to implement this but we don't use it.
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				//Each time the IPinput changes, if it is a correct IP:
				//UDP and TCP thread stops and restarts with new IP
				tcpConnection.stop();

				if (isValidIP(ipInput.getText().toString())) {

					//Stop old threads
					UDPThread.interrupt();

					//Create new threads with correct IP
					updateUDP();
					updateTCP();

					//Runs the new Threads
					UDPThread = initializeUDPThread();
					UDPThread.start();
					tcpConnection.execute();

					//Update the "old" IP address
					oldIP = ipInput.getText().toString();
				}
			}

			// We need to implement this but we don't use it.
			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}


	/**
	 * Initializes the UDPThread.
	 *
	 * @return the initialized UDPThread.
	 */
	private Thread initializeUDPThread() {
		//This thread runs the udp sending code
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						//TODO: Remove this later?
						synchronized (this) {
							// We want constant UDP-sendings when the UDP-server library has been updated
							//Also: Ugly code?
							wait(100);
						}
					} catch (InterruptedException e) {
						//e.printStackTrace();
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

	/**
	 * Checks whether the given input is a valid ip.address.
	 *
	 * @param input the given input.
	 * @return true if the given input is a valid ip-address, otherwise return false.
	 */
	private boolean isValidIP(String input) {
		Pattern p = Pattern.compile(
				"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)" +
						"{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
		return p.matcher(input).matches();
	}

	/**
	 * Sets the {@link MainActivity#tcpConnection} to a new
	 */
	private void updateTCP() {
		String newIP = oldIP;
		if (isValidIP(ipInput.getText().toString())) {
			newIP = ipInput.getText().toString();
		}
		if (newIP != null && !newIP.equals("")) {
			tcpConnection = new TCPConnection(newIP, PORT);
		}
	}

	/**
	 * Creates a new UDPConnection.
	 */
	private void updateUDP() {
		String newIP = oldIP;

		if (isValidIP(ipInput.getText().toString())) {
			newIP = ipInput.getText().toString();
		}

		if (newIP != null && !newIP.equals("")) {
			udpSender = createUDPSender(newIP, PORT);
		}
	}

	/**
	 * Creates a new {@link UDPSender} with the given ip-address and the given port.
	 * Note: If the given ip-address doesn't match a ip-address on the network the method will
	 * return null.
	 * Note: If the given port is already in use the method will return null.
	 *
	 * @param ip   the given ip-address.
	 * @param port the given port.
	 * @return a new UDPSender with the given ip-address and port if it can, otherwise null.
	 */
	private UDPSender createUDPSender(String ip, int port) {
		//Creates the UDPSender object with an IP address and port number
		UDPSender udpSender = null;
		try {
			udpSender = new UDPSender(ip, port);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			Log.e(this.getClass().getName(), "Unable to create InetAddress");
			e.printStackTrace();
		}
		return udpSender;
	}
}
