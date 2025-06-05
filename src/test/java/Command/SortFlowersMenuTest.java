package Command;

import models.Bouquet;
import models.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortFlowersMenuTest {
    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquet = new Bouquet(1);
        bouquet.addFlower(new Flower("Rose", 30.0, 3, 25.0));
        bouquet.addFlower(new Flower("Tulip", 20.0, 1, 15.0));
        bouquet.addFlower(new Flower("Lily", 25.0, 5, 20.0));
    }

    @Test
    public void testExecute_SortsFlowersByFreshness() {
        SortFlowersMenu command = new SortFlowersMenu(bouquet);
        command.execute();

        List<Flower> flowers = bouquet.getFlowers();
        assertEquals(1, flowers.get(0).getFreshnessLevel());
        assertEquals(3, flowers.get(1).getFreshnessLevel());
        assertEquals(5, flowers.get(2).getFreshnessLevel());
    }
}