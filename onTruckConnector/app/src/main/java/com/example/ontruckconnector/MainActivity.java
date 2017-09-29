package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

	//All UI-elements. Initializes in onCreate()
	private Button sendButton;
	private EditText messageText;
	private TextView connectionText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initializes the UI-elements
		sendButton = (Button)findViewById(R.id.sendButton);
		messageText = (EditText)findViewById(R.id.messageText);
		connectionText = (TextView)findViewById(R.id.connectionText);

		//Initializes a Holder-object to avoid double coupling between MainActivity and TCPChecker
		ConnectionTextHolder connectionTextHolder = ConnectionTextHolder.getInstance();
		connectionTextHolder.setTextView(connectionText);


		//Gives the button the ability to send the textfield's text to the UDPChecker
		sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String message = messageText.getText().toString();
				UDPSender.getInstance().sendMessage(message.getBytes());
			}
		});


		//Excecutes the TCP-connection
        new TCPChecker("192.168.43.150", 8721).execute();
    }


}
