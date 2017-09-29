package com.example.ontruckconnector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MessageConstructorTest {

        @Test
        public void messageIsCorrect() {
            byte[] bytes = {1, 50, Byte.parseByte("7F"), 07, 91, 04 };
            MessageConstructor constructor = new MessageConstructor();
            byte[] result = constructor.constructMessage('P', Byte.parseByte("7F"));
            assertEquals(result, bytes);
        }
}
