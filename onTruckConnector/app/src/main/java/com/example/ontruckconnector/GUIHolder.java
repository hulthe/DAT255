package com.example.ontruckconnector;


import android.widget.TextView;
import android.widget.ToggleButton;

class GUIHolder {

	private TextView connectionText;
	private ToggleButton toggleButton;
	private static GUIHolder GUIHolder;



	//This class is a singleton -> private constructor, only called once
	private GUIHolder(){}


	//First time this method is called the singleton object is created and stored statically
	static GUIHolder getInstance(){
		if(GUIHolder == null){
			GUIHolder = new GUIHolder();
		}
		return GUIHolder;
	}


	//The TextView is set here since it can't be set in the constructor
	void setTextView(TextView textView) {
		connectionText = textView;
	}

	void setToggleButton(ToggleButton toggleButton){
		this.toggleButton = toggleButton;
	}


	//This method is called from TCPConnection every "tick"
	void setConnection(boolean connection){
		toggleButton.setChecked(false);
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
