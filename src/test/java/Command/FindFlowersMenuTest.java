package Command;

import models.Bouquet;
import models.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FindFlowersMenuTest {

    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquet = new Bouquet(1);
        bouquet.addFlower(new Flower("Rose", 30.0, 4, 25.0));
        bouquet.addFlower(new Flower("Tulip", 20.0, 3, 15.0));
        bouquet.addFlower(new Flower("Lily", 40.0, 5, 30.0));
    }

    @Test
    public void testExecute_FindsMatchingFlowers() {
        FindFlowersMenu command = new FindFlowersMenu(bouquet, 25.0, 35.0);
        command.execute();

        List<Flower> matched = command.getMatchedFlowers();
        assertEquals(1, matched.size());
        assertEquals("Rose", matched.get(0).getFlowerType());
    }

    @Test
    public void testExecute_NoMatch() {
        FindFlowersMenu command = new FindFlowersMenu(bouquet, 50.0, 60.0);
        command.execute();

        List<Flower> matched = command.getMatchedFlowers();
        assertTrue(matched.isEmpty());
    }

    @Test
    public void testExecute_EmptyBouquet() {
        Bouquet emptyBouquet = new Bouquet(2);
        FindFlowersMenu command = new FindFlowersMenu(emptyBouquet, 20.0, 40.0);
        command.execute();

        List<Flower> matched = command.getMatchedFlowers();
        assertTrue(matched.isEmpty());
    }
}