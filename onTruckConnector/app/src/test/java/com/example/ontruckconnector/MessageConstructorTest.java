package com.example.ontruckconnector;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


public class MessageConstructorTest {

        @Test
        public void messageIsCorrect() {
            byte[] bytes = {0x01, 0x50, (byte)0x7F, 0x00, (byte)0xF3, (byte)0x81, 0x04 };
            MessageConstructor constructor = new MessageConstructor();
            byte[] result = constructor.constructMessage('P', (byte)0x7F, (byte)0x00);

            assertArrayEquals(result, bytes);
        }
}
