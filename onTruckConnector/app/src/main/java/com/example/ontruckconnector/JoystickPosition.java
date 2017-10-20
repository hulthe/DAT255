package com.example.ontruckconnector;

import android.util.Log;

/**
 * This class converts the joystick information from strength+combo to x+y value.
 * Theese values are then saved here.
 */
public class JoystickPosition {

	/**
	 * The X value. Positive when forward or negative when backwards. (-100, 100)
	 */
	private byte x = 0;

	/**
	 * The Y value. Positive when left or negative when right. (-100, 100)
	 */
	private byte y = 0;


	/**
	 * This class has an empty constructor.
	 */
	JoystickPosition(){}


	/**
	 * This method updates the X and Y values after converting an Angle and Strength value.
	 */
	public void onUpdate(int angle, int strength){
		//This converts the circular maximum of the joystick to a square
		//So you can reach ex: (100,100) instead of (100/sqrt(2), 100/sqrt(2))
		int newStrength = (int) ((strength * Math.sqrt(20000)) / 100);
		double dx = Math.cos(angle*Math.PI / 180) * newStrength;
		double dy = Math.sin(angle*Math.PI / 180) * newStrength;

		if(dx > 100){dx = 100;}
		else if(dx < -100){dx = -100;}
		x = (byte)dx;

		if(dy > 100){dy = 100;}
		else if(dy < -100){dy = -100;}
		y = (byte)dy;
	}


	/**
	 * Returns the X value.
	 * @return X
	 */
	public byte getX(){
		return x;
	}

	/**
	 * Returns the Y Value
	 * @return
	 */
	public byte getY(){
		return y;
	}
}
