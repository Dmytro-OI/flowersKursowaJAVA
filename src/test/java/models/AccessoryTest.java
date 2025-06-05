package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccessoryTest {

    @Test
    public void testConstructorAndGetters() {
        Accessory accessory = new Accessory("Ribbon", 10.0);
        assertEquals("Ribbon", accessory.getName());
        assertEquals(10.0, accessory.getPrice());
    }

    @Test
    public void testToString() {
        Accessory accessory = new Accessory("Wrapper", 15.0);
        String expected = "Accessory{name='Wrapper', price=15.0}";
        assertEquals(expected, accessory.toString());
    }

    @Test
    public void testNegativePrice() {
        Accessory accessory = new Accessory("Ribbon", -5.0);
        assertEquals(0.0, accessory.getPrice());
    }
}