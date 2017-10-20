package com.example.ontruckconnector;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.R.drawable.presence_busy;
import static android.R.drawable.presence_online;

/**
 * A singleton class used to avoid double dependencies when trying to change UI elements from different threads.
 */
class GUIHolder {

	//---------------------VARIABLES--------------------------------------

	/**
	 * The textView which either reads "Connected", if connected, or "Disconnected", if disconnected.
	 */
	private TextView connectionText;

	/**
	 * The togglebutton whcih turns on the ACC.
	 */
	private ToggleButton toggleButton;

	/**
	 * The little colored icon which shows the user connection/disconnection status
	 */
	private ImageView connectionImage;

	/**
	 * The colored border of the ToggleButton
	 */
	private ImageView borderImage;

	/**
	 *
	 */
	private boolean connectionBoolean = false;

	/**
	 * The static singleton object
	 */
	private static GUIHolder GUIHolder;

	//---------------------CONSTRUCTOR-------------------------------------

	/**
	 * Since this class is a singleton the constructor is private.
	 */
	private GUIHolder(){}


	//----------------------GETTERS---------------------------------------

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
	 * Simply return if the app is connected over TCP or not.
	 * @return
	 */
	boolean getConnection(){
		return connectionBoolean;
	}


	//----------------------SETTERS---------------------------------------

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
	 * This method is called from Main where it gives this class the red/green icon.
	 */
	void setImageView(ImageView imageView){
		this.connectionImage = imageView;
	}

	/**
	 * Private method used to set the text of the textView as long as it isn't null.
	 * @param text
	 */
	private void setConnectionText(final String text) {
		if(connectionText != null && connectionImage != null){
			connectionText.setText(text);
		}
	}

	/**
	 * This method is used by Main to give the correct ImageView to this class.
	 * @param imageView
	 */
	void setBorderImage(ImageView imageView){
		this.borderImage = imageView;
	}

	/**
	 * This method is called from TCPConnection every "tick".
	 * @param connection If true then the text becomes "Connected", otherwise "Disconnected"
	 */
	void setConnection(boolean connection){
		updateConnectionImage(connection);
		updateConnectionBoolean(connection);
		updateToggleButton(connection);

		if(connection){
			setConnectionText("Connected");
		}
		else{
			setConnectionText("Disconnected");
		}
	}


	//----------------------UPDATERS-------------------------------------

	/**
	 * This method saves the boolean ("is connected to TCP server") locally in this class
	 * @param connectionBoolean
	 */
	private void updateConnectionBoolean(final boolean connectionBoolean) {
		this.connectionBoolean = connectionBoolean;
	}

	/**
	 * This method manually updates the state of the small circular icon.
	 * If connected: it is green.
	 * If disconnected: it is red.
	 * @param connectionBoolean
	 */
	private void updateConnectionImage(final boolean connectionBoolean) {
		if(connectionImage != null){
			if (connectionBoolean) {
				connectionImage.setImageResource(presence_online);
			}else{
				connectionImage.setImageResource(presence_busy);
			}
		}
	}

	/**
	 * This method updates the state of the ToggleButton
	 * @param connectionBoolean
	 */
	private void updateToggleButton(final boolean connectionBoolean){
		if(!connectionBoolean){
			GUIHolder.getInstance().resetToggleButton();
		}else{
			toggleButton.setChecked(true);
			toggleButton.setChecked(false);
		}
		toggleButton.setEnabled(connectionBoolean);
	}


	//-----------------------OTHER---------------------------------------

	/**
	 * This method puts the ToggleButton back in the disconnected state.
	 */
	public void resetToggleButton(){
		borderImage.setBackgroundColor(Color.parseColor("#222222"));
		toggleButton.setBackgroundColor(Color.parseColor("#AAAAAA"));
		toggleButton.setTextColor(Color.parseColor("#000000"));
		toggleButton.setText("ACC is OFF");
	}

	/**
	 * This method puts the ToggleButton in the "connected over TCP but ACC is OFF"-state.
	 */
	public void deacativateToggleButton() {
		borderImage.setBackgroundColor(Color.parseColor("#FF1010"));
		borderImage.setBackgroundColor(Color.parseColor("#AA1010"));
		toggleButton.setText("ACC is asdasd");
	}

	/**
	 * This method puts the TOggleButton in the "connected over TCP and ACC is ON"-state.
	 */
	public void activateToggleButton() {
		borderImage.setBackgroundColor(Color.parseColor("#10FF10"));
		borderImage.setBackgroundColor(Color.parseColor("#10AA10"));
		toggleButton.setText("ACC is asdas");
	}


}
