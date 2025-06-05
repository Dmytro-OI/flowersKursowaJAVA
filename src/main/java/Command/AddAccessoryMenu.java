package Command;

import models.Bouquet;
import models.Accessory;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class AddAccessoryMenu implements Command {
    private final Bouquet bouquet;
    private final String accessoryName;
    private final double accessoryPrice;
    private static final Logger logger = LoggerConfig.getLogger();

    public AddAccessoryMenu(Bouquet bouquet, String accessoryName, double accessoryPrice) {
        this.bouquet = bouquet;
        this.accessoryName = accessoryName;
        this.accessoryPrice = accessoryPrice;
    }

    @Override
    public void execute() {
        if (accessoryPrice <= 0) {
            logger.warning("Неможливо додати аксесуар з невалідною ціною: " + accessoryPrice);
            return;
        }
        Accessory newAccessory = new Accessory(accessoryName, accessoryPrice);
        bouquet.addAccessory(newAccessory);
        logger.info("Додано аксесуар до букета #" + bouquet.getBouquetId() + ": " + newAccessory);
    }
}