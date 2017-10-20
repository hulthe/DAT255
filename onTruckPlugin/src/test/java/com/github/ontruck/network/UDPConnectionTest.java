package com.github.ontruck.network;
import com.github.ontruck.network.UDPConnection;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UDPConnectionTest {

	@Test
	public void validationTest() {
		byte[] correct1 = new byte[] {0x01, 0x50,       0x7F, 0x00, (byte)0xF3, (byte)0x81, 0x04};
		byte[] correct2 = new byte[] {0x01, 0x42,       0x3F, 0x01, (byte)0x88, (byte)0xDB, 0x04};
		byte[] correct3 = new byte[] {0x01, 0x53, (byte)0xC1, 0x02,       0x18, (byte)0x2F, 0x04};

		byte[] incorrect1 = new byte[] {0x10, 0x50,       0x7F, 0x00, 0x07, (byte)0x91, 0x04};
		byte[] incorrect2 = new byte[] {0x01, 0x42,       0x3F, 0x01, 0x03,       0x5D, 0x40};
		byte[] incorrect3 = new byte[] {0x01, 0x53, (byte)0xC1, 0x02, 0x30, (byte)0x97, 0x04};
		byte[] incorrect4 = new byte[] {0x01, 0x51,       0x7F, 0x03, 0x07, (byte)0x91, 0x04};
		byte[] incorrect5 = new byte[] {0x01, 0x42,       0x3F, 0x04, 0x03,       0x5E, 0x04};

		assertTrue("Invalidated correct message", UDPConnection.validate(correct1));
		assertTrue("Invalidated correct message", UDPConnection.validate(correct2));
		assertTrue("Invalidated correct message", UDPConnection.validate(correct3));

		assertFalse("Validated incorrect starter",UDPConnection.validate(incorrect1));
		assertFalse("Validated incorrect terminator", UDPConnection.validate(incorrect2));
		assertFalse("Validated incorrect checksum, first byte", UDPConnection.validate(incorrect3));
		assertFalse("Validated incorrect type byte", UDPConnection.validate(incorrect4));
		assertFalse("Validated incorrect checksum, second byte", UDPConnection.validate(incorrect5));
	}

}
