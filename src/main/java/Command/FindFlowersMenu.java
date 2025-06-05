package Command;

import models.Bouquet;
import models.Flower;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class FindFlowersMenu implements Command {
    private final Bouquet bouquet;
    private final double minStemLength;
    private final double maxStemLength;
    private static final Logger logger = LoggerConfig.getLogger();
    private List<Flower> matchedFlowers = new ArrayList<>();

    public FindFlowersMenu(Bouquet bouquet, double minStemLength, double maxStemLength) {
        this.bouquet = bouquet;
        this.minStemLength = minStemLength;
        this.maxStemLength = maxStemLength;
    }

    @Override
    public void execute() {
        matchedFlowers.clear();
        for (Flower flower : bouquet.getFlowers()) {
            if (flower.getStemLength() >= minStemLength && flower.getStemLength() <= maxStemLength) {
                matchedFlowers.add(flower);
                logger.info("Знайдено квітку в букеті #" + bouquet.getBouquetId() + ": " + flower);
            }
        }

        if (matchedFlowers.isEmpty()) {
            logger.info("У букеті #" + bouquet.getBouquetId() + " немає квіток з довжиною стебла в діапазоні " + minStemLength + "–" + maxStemLength);
        }
    }

    public List<Flower> getMatchedFlowers() {
        return matchedFlowers;
    }
}