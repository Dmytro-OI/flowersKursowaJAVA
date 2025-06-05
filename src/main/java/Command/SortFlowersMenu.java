package Command;

import models.Bouquet;
import java.util.logging.Logger;
import logging.LoggerConfig;
import models.Flower;

public class SortFlowersMenu implements Command {
    private final Bouquet bouquet;
    private static final Logger logger = LoggerConfig.getLogger();

    public SortFlowersMenu(Bouquet bouquet) {
        this.bouquet = bouquet;
    }

    @Override
    public void execute() {
        bouquet.sortFlowersByFreshness();
        logger.info("Квітки букета #" + bouquet.getBouquetId() + " відсортовано за свіжістю.");

        for (Flower flower : bouquet.getFlowers()) {
            logger.fine("Стан після сортування: " + flower.toString());
        }
    }
}