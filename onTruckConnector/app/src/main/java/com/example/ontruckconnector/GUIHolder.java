package com.example.ontruckconnector;

import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A singleton class used to avoid double dependencies when trying to change UI elements from different threads.
 */
class GUIHolder {

	/**
	 * The textView which either reads "Connected", if connected, or "Disconnected", if disconnected.
	 */
	private TextView connectionText;

	/**
	 * The togglebutton whcih turns on the ACC.
	 */
	private ToggleButton toggleButton;

	/**
	 * The static singleton object
	 */
	private static GUIHolder GUIHolder;


	/**
	 * Since this class is a singleton the constructor is private.
	 */
	private GUIHolder(){}


	/**
	 * First time this method is called the singleton object is created and stored statically.
	 * @return Either a new GUIHolder object or the already created one.
	 */
	static GUIHolder getInstance(){
		if(GUIHolder == null){
			GUIHolder = new GUIHolder();
		}
		return GUIHolder;
	}


	/**
	 * The TextView is set here since it can't be set in the constructor.
	 * @param textView
	 */
	void setTextView(TextView textView) {
		connectionText = textView;
	}

	/**
	 * The ToggleButton is set here since it can't be set in the constructor.
	 * @param toggleButton
	 */
	void setToggleButton(ToggleButton toggleButton){
		this.toggleButton = toggleButton;
	}


	/**
	 * This method is called from TCPConnection every "tick".
	 * @param connection If true then the text becomes "Connected", otherwise "Disconnected"
	 */
	void setConnection(boolean connection){
		toggleButton.setChecked(false);
		if(connection){
			setConnectionText("Connected");}
		else{
			setConnectionText("Disconnected");
		}
	}

	/**
	 * Private method used to set the text of the textView as long as it isn't null.
	 * @param text
	 */
	//Simply sets TextView's text = the input string
	private void setConnectionText(final String text) {
		if(connectionText != null){
			connectionText.setText(text);
		}
	}
}
