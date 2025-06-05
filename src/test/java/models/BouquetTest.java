package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BouquetTest {
    private Bouquet bouquet;
    private Flower f1;
    private Flower f2;
    private Accessory a1;
    private Accessory a2;

    @BeforeEach
    public void setUp() {
        bouquet = new Bouquet(1);
        f1 = new Flower("Rose", 30.0, 5, 10.0);
        f2 = new Flower("Tulip", 25.0, 3, 8.0);
        a1 = new Accessory("Ribbon", 2.0);
        a2 = new Accessory("Card", 1.0);
    }

    @Test
    public void testAddAndRemoveFlowerByObject() {
        bouquet.addFlower(f1);
        assertEquals(1, bouquet.getFlowers().size());

        bouquet.removeFlower(f1);
        assertEquals(0, bouquet.getFlowers().size());

        bouquet.removeFlower(f2);
        assertEquals(0, bouquet.getFlowers().size());
    }

    @Test
    public void testRemoveFlowerByIndex() {
        bouquet.addFlower(f1);
        bouquet.addFlower(f2);
        assertEquals(2, bouquet.getFlowers().size());

        bouquet.removeFlower(0);
        assertEquals(1, bouquet.getFlowers().size());
        assertEquals(f2, bouquet.getFlowers().get(0));

        bouquet.removeFlower(5);
        assertEquals(1, bouquet.getFlowers().size());
    }

    @Test
    public void testAddAndRemoveAccessory() {
        bouquet.addAccessory(a1);
        assertEquals(1, bouquet.getAccessories().size());

        bouquet.removeAccessory(a1);
        assertEquals(0, bouquet.getAccessories().size());

        bouquet.removeAccessory(a2);
        assertEquals(0, bouquet.getAccessories().size());
    }

    @Test
    public void testCalculateTotalCost() {
        bouquet.addFlower(f1);
        bouquet.addFlower(f2);
        bouquet.addAccessory(a1);
        bouquet.addAccessory(a2);

        double expected = 10.0 + 8.0 + 2.0 + 1.0;
        assertEquals(expected, bouquet.calculateTotalCost(), 0.0001);
    }

    @Test
    public void testCalculateTotalFreshness() {
        bouquet.addFlower(f1);
        bouquet.addFlower(f2);
        int expected = 5 + 3;
        assertEquals(expected, bouquet.calculateTotalFreshness());
    }

    @Test
    public void testSortFlowersByFreshness() {
        bouquet.addFlower(f1);
        bouquet.addFlower(f2);
        bouquet.sortFlowersByFreshness();
        assertEquals(f2, bouquet.getFlowers().get(0));
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(1, bouquet.getBouquetId());
        bouquet.setBouquetId(10);
        assertEquals(10, bouquet.getBouquetId());
    }

    @Test
    public void testUnmodifiableLists() {
        bouquet.addFlower(f1);
        bouquet.addAccessory(a1);

        assertThrows(UnsupportedOperationException.class, () -> {
            bouquet.getFlowers().add(f2);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            bouquet.getAccessories().add(a2);
        });
    }
}
