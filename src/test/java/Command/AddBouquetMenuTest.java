package Command;

import models.Bouquet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddBouquetMenuTest {

    private List<Bouquet> bouquets;

    @BeforeEach
    public void setUp() {
        bouquets = new ArrayList<>();
    }

    @Test
    public void testExecute_AddsBouquetToList() {
        AddBouquetMenu command = new AddBouquetMenu(bouquets, 1);
        command.execute();

        assertEquals(1, bouquets.size());
        assertEquals(1, bouquets.get(0).getBouquetId());
    }
}