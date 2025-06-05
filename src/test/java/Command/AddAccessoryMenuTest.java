package Command;

import models.Bouquet;
import models.Accessory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddAccessoryMenuTest {

    private List<Bouquet> bouquets;
    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        bouquets = new ArrayList<>();
        bouquet = new Bouquet(1);
        bouquets.add(bouquet);
    }

    @Test
    public void testExecute_AddsAccessoryToBouquet() {
        AddAccessoryMenu command = new AddAccessoryMenu(bouquet, "Ribbon", 10.0);
        command.execute();

        List<Accessory> accessories = bouquet.getAccessories();
        assertEquals(1, accessories.size());
        Accessory accessory = accessories.get(0);
        assertEquals("Ribbon", accessory.getName());
        assertEquals(10.0, accessory.getPrice());
    }

    @Test
    public void testExecute_NegativePrice_DoesNotAdd() {
        AddAccessoryMenu command = new AddAccessoryMenu(bouquet, "Ribbon", -5.0);
        command.execute();

        List<Accessory> accessories = bouquet.getAccessories();
        assertEquals(0, accessories.size());
    }

    @Test
    public void testExecute_ZeroPrice_DoesNotAdd() {
        AddAccessoryMenu command = new AddAccessoryMenu(bouquet, "Ribbon", 0.0);
        command.execute();

        List<Accessory> accessories = bouquet.getAccessories();
        assertEquals(0, accessories.size());
    }
}
