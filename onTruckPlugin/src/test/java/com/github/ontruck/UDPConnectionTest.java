package com.github.ontruck;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class UDPConnectionTest {

	@Test
	public void validationTest() {
		byte[] correct1 = new byte[] {0x01, 0x50, 0x7F, 0x07, (byte) 0x91, 0x04};
		byte[] correct2 = new byte[] {0x01, 0x42, 0x3F, 0x03, 0x5D, 0x04};
		byte[] correct3 = new byte[] {0x01, 0x53, (byte) 0xC1, 0x29, (byte) 0x97, 0x04};

		byte[] incorrect1 = new byte[] {0x10, 0x50, 0x7F, 0x07, (byte) 0x91, 0x04};
		byte[] incorrect2 = new byte[] {0x01, 0x42, 0x3F, 0x03, 0x5D, 0x40};
		byte[] incorrect3 = new byte[] {0x01, 0x53, (byte) 0xC1, 0x30, (byte) 0x97, 0x04};
		byte[] incorrect4 = new byte[] {0x01, 0x51, 0x7F, 0x07, (byte) 0x91, 0x04};
		byte[] incorrect5 = new byte[] {0x01, 0x42, 0x3F, 0x03, 0x5E, 0x04};

		assertTrue("Invalidated correct message", UDPConnection.validate(correct1));
		assertTrue("Invalidated correct message", UDPConnection.validate(correct2));
		assertTrue("Invalidated correct message", UDPConnection.validate(correct3));

		assertTrue("Validated incorrect starter",!UDPConnection.validate(incorrect1));
		assertTrue("Validated incorrect terminator", !UDPConnection.validate(incorrect2));
		assertTrue("Validated incorrect checksum, first byte", !UDPConnection.validate(incorrect3));
		assertTrue("Validated incorrect type byte", !UDPConnection.validate(incorrect4));
		assertTrue("Validated incorrect checksum, second byte", !UDPConnection.validate(incorrect5));
	}

}
