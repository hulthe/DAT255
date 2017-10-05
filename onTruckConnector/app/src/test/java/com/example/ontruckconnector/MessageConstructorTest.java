package com.example.ontruckconnector;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class MessageConstructorTest {

        @Test
        public void messageIsCorrect() {
            byte[] bytes = {0x01, 0x50, (byte)0x7F,0x07, (byte)0x91, 0x04 };
            MessageConstructor constructor = new MessageConstructor();
            byte[] result = constructor.constructMessage('P', (byte) 0x7F);

            assertArrayEquals(result, bytes);
        }
}
