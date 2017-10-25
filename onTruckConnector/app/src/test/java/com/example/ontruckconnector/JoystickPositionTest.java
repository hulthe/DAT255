package com.example.ontruckconnector;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * This is a testing class for testing the logic inside JoystickPosition
 */
public class JoystickPositionTest {

	/**
	 * Construct a joystickPosition and default values are correct.
	 */
	@Test
	public void testConstructor(){
		JoystickPosition joystickPosition = new JoystickPosition();
		assertEquals("joystickPosition.x == 0 after construction", joystickPosition.getX(), 0);
		assertEquals("joystickPosition.y == 0 after construction", joystickPosition.getY(), 0);
	}

	/**
	 * Joystick position always stays in the range = {-100, -99 ... 99, 100}.
	 */
	@Test
	public void testRange() {
		JoystickPosition joystickPosition = new JoystickPosition();
		for (int strength = 0; strength < 200; strength++) {
			for (int angle = 0; angle < 400; angle++) {
				joystickPosition.onUpdate(angle, strength);
				assertTrue("" + joystickPosition.getX(), joystickPosition.getX() < 101);
				assertTrue("" + joystickPosition.getX(), joystickPosition.getX() > -101);
				assertTrue("" + joystickPosition.getY(), joystickPosition.getY() < 101);
				assertTrue("" + joystickPosition.getY(), joystickPosition.getY() > -101);
			}
		}
	}

	/**
	 * If the strength is 100, at least one of the values X and Y are 100 or -100
	 */
	@Test
	public void testMaxStrength(){
		JoystickPosition joystickPosition = new JoystickPosition();

		int strength = 100;
		for(int angle=0; angle<400; angle++){
			joystickPosition.onUpdate(angle, strength);
			assertTrue("Values: X:"+joystickPosition.getX()+" || Y:"+joystickPosition.getY(),
					joystickPosition.getX() == 100 || joystickPosition.getX() == -100 ||
					joystickPosition.getY() == 100 ||joystickPosition.getY() == -100);

		}
	}

	/**
	 * This proves that the circular controls of the joystick results in a square with
	 * 				the four corners: (100,100) | (100,-100) | (-100,100) | (-100, -100).
	 */
	@Test
	public void testCorners(){
		JoystickPosition joystickPosition = new JoystickPosition();

		joystickPosition.onUpdate(45, 100);
		assertEquals("TopRight - X = Y", joystickPosition.getX(), joystickPosition.getY());
		assertEquals("TopRight - X = 100", joystickPosition.getX(), 100);

		joystickPosition.onUpdate(135, 100);
		assertEquals("TopLeft - X = -Y", joystickPosition.getX(), -1 * joystickPosition.getY());
		assertEquals("TopLeft - X = -100", joystickPosition.getX(), -100);

		joystickPosition.onUpdate(225, 100);
		assertEquals("BotLeft - X = Y", joystickPosition.getX(), joystickPosition.getY());
		assertEquals("BotLeft - X = -100", joystickPosition.getX(), -100);

		joystickPosition.onUpdate(315, 100);
		assertEquals("BotRIght - X = -Y", joystickPosition.getX(), -1 * joystickPosition.getY());
		assertEquals("BotRight - X = 100", joystickPosition.getX(), 100);
	}

	/**
	 * If the strength increases -> The absolute values of X and Y either increases or stays the same.
	 */
	@Test
	public void testStrengthIncrease() {
		JoystickPosition joystickPosition = new JoystickPosition();

		for (int angle = 0; angle < 400; angle++) {
			int oldX = 0;
			int oldY = 0;
			for (int strength = 0; strength < 200; strength++) {
				joystickPosition.onUpdate(angle, strength);
				assertTrue("Last X value:" + oldX + " | New X value:" + Math.abs(joystickPosition.getX()), oldX <= Math.abs(joystickPosition.getX()));
				assertTrue("Last Y value:" + oldY + " | New Y value:" + Math.abs(joystickPosition.getY()), oldY <= Math.abs(joystickPosition.getY()));
				oldX = joystickPosition.getX();
				oldY = joystickPosition.getY();
			}
		}
	}
}
