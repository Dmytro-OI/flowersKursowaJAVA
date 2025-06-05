package Command;

import models.Bouquet;
import models.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CalculateTotalCostMenuTest {

    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquet = new Bouquet(1);
        bouquet.addFlower(new Flower("Rose", 30.0, 4, 25.0));
        bouquet.addFlower(new Flower("Tulip", 20.0, 3, 15.0));
    }

    @Test
    public void testExecute_CorrectTotalCostCalculated() {
        CalculateTotalCostMenu command = new CalculateTotalCostMenu(bouquet);
        command.execute();

        double expected = 25.0 + 15.0;
        assertEquals(expected, bouquet.calculateTotalCost());
    }
}