package Command;

import models.Bouquet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteBouquetMenuTest {

    private List<Bouquet> bouquets;

    @BeforeEach
    public void setUp() {
        bouquets = new ArrayList<>();
        bouquets.add(new Bouquet(1));
        bouquets.add(new Bouquet(2));
    }

    @Test
    public void testExecute_RemovesCorrectBouquet() {
        DeleteBouquetMenu command = new DeleteBouquetMenu(bouquets, 1);
        command.execute();

        assertEquals(1, bouquets.size());
        assertEquals(2, bouquets.get(0).getBouquetId());
    }

    @Test
    public void testExecute_NonexistentBouquet_DoesNothing() {
        DeleteBouquetMenu command = new DeleteBouquetMenu(bouquets, 999);
        command.execute();

        assertEquals(2, bouquets.size());
    }
}