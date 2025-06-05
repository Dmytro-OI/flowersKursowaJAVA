package Command;

import models.Bouquet;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class CalculateTotalCostMenu implements Command {
    private final Bouquet bouquet;
    private static final Logger logger = LoggerConfig.getLogger();

    public CalculateTotalCostMenu(Bouquet bouquet) {
        this.bouquet = bouquet;
    }

    @Override
    public void execute() {
        double total = bouquet.calculateTotalCost();
        logger.info("Загальна вартість букета #" + bouquet.getBouquetId() + ": " + total + " грн");
    }
}