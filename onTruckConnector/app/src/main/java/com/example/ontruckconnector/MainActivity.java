package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class MainActivity extends AppCompatActivity {

	//All UI-elements. Initializes in onCreate()
	private TextView connectionText;
	private JoystickPosition joystickPosition = new JoystickPosition();
	private JoystickView joystickView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initializes the UI-elements
		connectionText = (TextView)findViewById(R.id.connectionText);
		joystickView = (JoystickView)findViewById(R.id.joystick);

		//Initializes a Holder-object to avoid double coupling between MainActivity and TCPChecker
		ConnectionTextHolder connectionTextHolder = ConnectionTextHolder.getInstance();
		connectionTextHolder.setTextView(connectionText);

		joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {

			@Override
			public void onMove(int angle, int strength) {
				joystickPosition.onUpdate(angle, strength);
			}
		});



		//Excecutes the TCP-connection
        new TCPChecker("192.168.43.150", 8721).execute();
    }


}
