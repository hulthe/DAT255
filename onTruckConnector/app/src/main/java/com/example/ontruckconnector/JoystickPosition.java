package com.example.ontruckconnector;



public class JoystickPosition {

	private byte x = 0;
	private byte y = 0;


	JoystickPosition(){
	}


	public void onUpdate(int angle, int strength){
		byte angleByte = (byte) angle;
		byte strengthByte = (byte) strength;
		x = (byte) (Math.cos(angleByte) * strengthByte);
		y = (byte) (Math.sin(angleByte) * strengthByte);
	}


	public byte getX(){
		return x;
	}

	public byte getY(){
		return y;
	}
}
