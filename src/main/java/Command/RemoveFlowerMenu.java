package Command;

import models.Bouquet;
import models.Flower;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class RemoveFlowerMenu implements Command {
    private final Bouquet bouquet;
    private final int index;
    private static final Logger logger = LoggerConfig.getLogger();

    public RemoveFlowerMenu(Bouquet bouquet, int index) {
        this.bouquet = bouquet;
        this.index = index;
    }

    @Override
    public void execute() {
        if (bouquet.getFlowers().isEmpty()) {
            logger.warning("Букет #" + bouquet.getBouquetId() + " порожній, видалити квітку неможливо.");
            return;
        }

        if (index < 0 || index >= bouquet.getFlowers().size()) {
            logger.warning("Некоректний індекс " + index + " для букета #" + bouquet.getBouquetId() +
                    " (розмір списку: " + bouquet.getFlowers().size() + ")");
            return;
        }

        Flower removedFlower = bouquet.getFlowers().get(index);
        bouquet.removeFlower(index);
        logger.info("Квітку успішно видалено з букета #" + bouquet.getBouquetId() + ": " + removedFlower);
    }
}