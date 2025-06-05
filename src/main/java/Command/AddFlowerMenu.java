package Command;

import models.Bouquet;
import models.Flower;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class AddFlowerMenu implements Command {
    private final Bouquet bouquet;
    private final String flowerType;
    private final double stemLength;
    private final int freshnessLevel;
    private final double price;
    private static final Logger logger = LoggerConfig.getLogger();

    public AddFlowerMenu(Bouquet bouquet, String flowerType, double stemLength, int freshnessLevel, double price) {
        this.bouquet = bouquet;
        this.flowerType = flowerType;
        this.stemLength = stemLength;
        this.freshnessLevel = freshnessLevel;
        this.price = price;
    }

    @Override
    public void execute() {
        Flower newFlower = new Flower(flowerType, stemLength, freshnessLevel, price);
        bouquet.addFlower(newFlower);
        logger.info("Додано квітку до букета #" + bouquet.getBouquetId() + ": " + newFlower);
    }
}