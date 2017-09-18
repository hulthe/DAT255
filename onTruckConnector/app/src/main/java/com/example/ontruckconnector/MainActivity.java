package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

	private Button sendButton;
	private EditText messageText;
	private TextView connectionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sendButton = (Button)findViewById(R.id.sendButton);
		messageText = (EditText)findViewById(R.id.messageText);
		connectionText = (TextView)findViewById(R.id.connectionText);

		ConnectionTextHolder connectionTextHolder = ConnectionTextHolder.getInstance();
		connectionTextHolder.setTextView(connectionText);

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
