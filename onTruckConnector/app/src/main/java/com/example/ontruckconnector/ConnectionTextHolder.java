package com.example.ontruckconnector;


import android.widget.TextView;

class ConnectionTextHolder {

	private TextView connectionText;
	private static ConnectionTextHolder connectionTextHolder;


	//This class is a singleton -> private constructor, only called once
	private ConnectionTextHolder(){}


	//First time this method is called the singleton object is created and stored statically
	static ConnectionTextHolder getInstance(){
		if(connectionTextHolder == null){
			connectionTextHolder = new ConnectionTextHolder();
		}
		return connectionTextHolder;
	}


	//The TextView is set here since it can't be set in the constructor
	void setTextView(TextView textView) {
		connectionText = textView;

	}

	//This method is called from TCPChecker every "tick"
	void setConnection(boolean connection){
		if(connection){
			setConnectionText("Connected");}
		else{
			setConnectionText("Disconnected");
		}
	}

	//Simply sets TextView's text = the input string
	private void setConnectionText(final String text) {
		if(connectionText != null){
			connectionText.setText(text);
		}
	}

}
