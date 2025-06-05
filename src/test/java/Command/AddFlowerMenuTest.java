package Command;

import models.Bouquet;
import models.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddFlowerMenuTest {

    private List<Bouquet> bouquets;
    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquets = new ArrayList<>();
        bouquet = new Bouquet(1);
        bouquets.add(bouquet);
    }

    @Test
    public void testExecute_AddsFlowerToBouquet() {
        AddFlowerMenu command = new AddFlowerMenu(bouquet, "Rose", 30.0, 4, 25.0);
        command.execute();

        List<Flower> flowers = bouquet.getFlowers();
        assertEquals(1, flowers.size());
        Flower flower = flowers.get(0);
        assertEquals("Rose", flower.getFlowerType());
        assertEquals(30.0, flower.getStemLength());
        assertEquals(4, flower.getFreshnessLevel());
        assertEquals(25.0, flower.getPrice());
    }
}