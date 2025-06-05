package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlowerTest {

    @Test
    public void testConstructorAndGetters() {
        Flower flower = new Flower("Rose", 30.0, 4, 25.0);
        assertEquals("Rose", flower.getFlowerType());
        assertEquals(30.0, flower.getStemLength());
        assertEquals(4, flower.getFreshnessLevel());
        assertEquals(25.0, flower.getPrice());
    }

    @Test
    public void testToString() {
        Flower flower = new Flower("Tulip", 20.0, 3, 15.0);
        String expected = "Flower{flowerType='Tulip', stemLength=20.0, freshnessLevel=3, price=15.0}";
        assertEquals(expected, flower.toString());
    }

    @Test
    public void testInvalidFreshness() {
        assertThrows(IllegalArgumentException.class, () -> new Flower("Rose", 30.0, 6, 25.0));
    }
}