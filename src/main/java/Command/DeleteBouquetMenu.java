package Command;

import models.Bouquet;
import java.util.List;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class DeleteBouquetMenu implements Command {
    private final List<Bouquet> bouquets;
    private final int bouquetId;
    private static final Logger logger = LoggerConfig.getLogger();

    public DeleteBouquetMenu(List<Bouquet> bouquets, int bouquetId) {
        this.bouquets = bouquets;
        this.bouquetId = bouquetId;
    }

    @Override
    public void execute() {
        Bouquet bouquetToDelete = findBouquetById(bouquetId);
        if (bouquetToDelete != null) {
            bouquets.remove(bouquetToDelete);
            logger.info("Букет з ID " + bouquetId + " видалено.");
        } else {
            logger.warning("Спроба видалити букет #" + bouquetId + ", якого не існує.");
        }
    }

    private Bouquet findBouquetById(int bouquetId) {
        for (Bouquet bouquet : bouquets) {
            if (bouquet.getBouquetId() == bouquetId) {
                return bouquet;
            }
        }
        return null;
    }
}