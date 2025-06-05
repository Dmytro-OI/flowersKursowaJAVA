package Command;

import models.Bouquet;
import models.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveFlowerMenuTest {

    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquet = new Bouquet(1);
        bouquet.addFlower(new Flower("Rose", 30.0, 4, 25.0));
        bouquet.addFlower(new Flower("Tulip", 20.0, 3, 15.0));
        bouquet.addFlower(new Flower("Rose", 30.0, 4, 25.0));
    }

    @Test
    public void testExecute_RemovesFlowerAtIndex() {
        RemoveFlowerMenu command = new RemoveFlowerMenu(bouquet, 0);
        command.execute();

        List<Flower> flowers = bouquet.getFlowers();
        assertEquals(2, flowers.size());
        assertEquals("Tulip", flowers.get(0).getFlowerType());
        assertEquals("Rose", flowers.get(1).getFlowerType());
    }

    @Test
    public void testExecute_RemovesMiddleFlower() {
        RemoveFlowerMenu command = new RemoveFlowerMenu(bouquet, 1);
        command.execute();

        List<Flower> flowers = bouquet.getFlowers();
        assertEquals(2, flowers.size());
        assertEquals("Rose", flowers.get(0).getFlowerType());
        assertEquals("Rose", flowers.get(1).getFlowerType());
    }

    @Test
    public void testExecute_InvalidIndex() {
        RemoveFlowerMenu command = new RemoveFlowerMenu(bouquet, 5);
        command.execute();

        List<Flower> flowers = bouquet.getFlowers();
        assertEquals(3, flowers.size());
    }

    @Test
    public void testExecute_EmptyBouquet() {
        Bouquet emptyBouquet = new Bouquet(2);
        RemoveFlowerMenu command = new RemoveFlowerMenu(emptyBouquet, 0);
        command.execute();

        List<Flower> flowers = emptyBouquet.getFlowers();
        assertEquals(0, flowers.size());
    }
}