package Command;

import models.Bouquet;
import java.util.List;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class AddBouquetMenu implements Command {
    private final List<Bouquet> bouquets;
    private final int bouquetId;
    private static final Logger logger = LoggerConfig.getLogger();

    public AddBouquetMenu(List<Bouquet> bouquets, int bouquetId) {
        this.bouquets = bouquets;
        this.bouquetId = bouquetId;
    }

    @Override
    public void execute() {
        bouquets.add(new Bouquet(bouquetId));
        logger.info("Букет з ID " + bouquetId + " додано до списку.");
    }
}