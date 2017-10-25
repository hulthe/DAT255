package com.github.ontruck.moped;

import com.github.ontruck.moped.Driver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DriverTest {

	@Test
	public void speedChangeTest(){
			//Driver driver = new Driver(null);
			byte[] usefulPVs = Driver.getUsefulPowerValues();
			byte lastPL = 0;
			byte newPL = 0;

			assertEquals("lastPowerValue starts at 0", 0, newPL);

			for(int i = 0; i<usefulPVs.length ;i++){
				newPL = Driver.calculateIncreaseSpeed(lastPL);

				assertTrue(String.format("new PL [%d] exists in byte array (1)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after increaseSpeed(), becomes greater",
						(lastPL < newPL) || newPL == 100);

				lastPL = newPL;
			}

			for(int i = (usefulPVs.length-1)*2; i>0 ;i--){
				newPL = Driver.calculateDecreaseSpeed(lastPL);

				assertTrue(String.format("new PL [%d] exists in byte array (2)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after decreaseSpeed(), becomes lesser",
						(lastPL > newPL) || newPL == -100);

				lastPL = newPL;
			}

			for(int i = 0; i<usefulPVs.length*2 ;i++){
				newPL = Driver.calculateIncreaseSpeed(lastPL);

				assertTrue(String.format("new PL [%d] exists in byte array (3)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after increaseSpeed(), becomes greater",
						(lastPL < newPL) || newPL == 100);

				lastPL = newPL;
			}
	}


	public boolean existsInByteArray(byte value, byte[] data) {
		for (byte arrayValue : data) {
			if (value == arrayValue || value == (arrayValue * -1)) {
				return true;
			}
		}
		return false;
	}
}
