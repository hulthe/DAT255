package com.example.ontruckconnector;


import android.util.Log;

//This class is only a holder an converter for the virtual joystick
//This class converts the joystick's information in strength+combo to a x+y value
public class JoystickPosition {

	private byte x = 0;
	private byte y = 0;


	JoystickPosition(){
	}


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


	public byte getX(){
		return x;
	}

	public byte getY(){
		return y;
	}
}
