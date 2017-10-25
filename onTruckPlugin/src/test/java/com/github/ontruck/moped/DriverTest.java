package com.github.ontruck.moped;

import com.github.ontruck.moped.Driver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DriverTest {

	@Test
	public void speedChangeTest(){
		try{
			Driver driver = new Driver(null);
			byte[] usefulPVs = driver.getUsefulPowerValues();
			byte lastPL = driver.getLastPowerValue();
			byte newPL = driver.getLastPowerValue();

			assertEquals("lastPowerValue starts at 0", 0, newPL);

			for(int i = 0; i<usefulPVs.length ;i++){
				driver.increaseSpeed();

				newPL = driver.getLastPowerValue();

				assertTrue(String.format("new PL [%d] exists in byte array (1)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after increaseSpeed(), becomes greater",
						(lastPL < newPL) || newPL == 100);

				lastPL = newPL;
			}

			for(int i = (usefulPVs.length-1)*2; i>0 ;i--){
				//driver.calculateDecreaseSpeed(lastPL);

				newPL = driver.calculateDecreaseSpeed(lastPL);

				assertTrue(String.format("new PL [%d] exists in byte array (2)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after decreaseSpeed(), becomes lesser",
						(lastPL > newPL) || newPL == -100);

				lastPL = newPL;
			}

			for(int i = 0; i<usefulPVs.length*2 ;i++){
				driver.increaseSpeed();

				newPL = driver.getLastPowerValue();

				assertTrue(String.format("new PL [%d] exists in byte array (3)", newPL),
						existsInByteArray(newPL, usefulPVs));
				assertTrue("new PL, after increaseSpeed(), becomes greater",
						(lastPL < newPL) || newPL == 100);

				lastPL = newPL;
			}

		}catch(IOException e){
			e.printStackTrace();
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
