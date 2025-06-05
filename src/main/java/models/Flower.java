package models;

import java.util.logging.Logger;
import logging.LoggerConfig;

public class Flower {
    private final String flowerType;
    private final double stemLength;
    private final int freshnessLevel;
    private final double price;
    private static final Logger logger = LoggerConfig.getLogger();

    public Flower(String flowerType, double stemLength, int freshnessLevel, double price) {
        if (freshnessLevel < 1 || freshnessLevel > 5) {
            throw new IllegalArgumentException("Рівень свіжості має бути від 1 до 5, отримано: " + freshnessLevel);
        }
        this.flowerType = flowerType;
        this.stemLength = stemLength;
        this.freshnessLevel = freshnessLevel;
        this.price = price;
        logger.info("Створено квітку: " + this);
    }

    public String getFlowerType() {
        return flowerType;
    }

    public double getStemLength() {
        return stemLength;
    }

    public int getFreshnessLevel() {
        return freshnessLevel;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Flower{flowerType='" + flowerType + "', stemLength=" + stemLength +
                ", freshnessLevel=" + freshnessLevel + ", price=" + price + "}";
    }
}