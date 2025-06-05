package models;

import logging.LoggerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class Bouquet {
    private static final Logger logger = LoggerConfig.getLogger();

    private int bouquetId;
    private List<Flower> flowers;
    private List<Accessory> accessories;

    public Bouquet(int bouquetId) {
        this.bouquetId = bouquetId;
        this.flowers = new ArrayList<>();
        this.accessories = new ArrayList<>();
    }

    public void addFlower(Flower flower) {
        flowers.add(flower);
        logger.info("Квітку додано до букета з ID " + bouquetId + ": " + flower);
    }

    public void removeFlower(Flower flower) {
        if (flowers.remove(flower)) {
            logger.info("Квітку видалено з букета з ID " + bouquetId + ": " + flower);
        } else {
            logger.warning("Спроба видалити квітку, якої не існує, з букета #" + bouquetId + ": " + flower);
        }
    }

    public void removeFlower(int index) {
        if (index >= 0 && index < flowers.size()) {
            Flower removedFlower = flowers.remove(index);
            logger.info("Квітку видалено з букета з ID " + bouquetId + " за індексом " + index + ": " + removedFlower);
        } else {
            logger.warning("Некоректний індекс " + index + " для видалення квітки з букета #" + bouquetId +
                    " (розмір списку: " + flowers.size() + ")");
        }
    }

    public void addAccessory(Accessory accessory) {
        accessories.add(accessory);
        logger.info("Аксесуар додано до букета з ID " + bouquetId + ": " + accessory);
    }

    public void removeAccessory(Accessory accessory) {
        if (accessories.remove(accessory)) {
            logger.info("Аксесуар видалено з букета з ID " + bouquetId + ": " + accessory);
        } else {
            logger.warning("Спроба видалити аксесуар, якого не існує, з букета #" + bouquetId + ": " + accessory);
        }
    }

    public List<Accessory> getAccessories() {
        return Collections.unmodifiableList(accessories);
    }

    public List<Flower> getFlowers() {
        return Collections.unmodifiableList(flowers);
    }

    public double calculateTotalCost() {
        double totalCost = 0;
        for (Flower flower : flowers) {
            totalCost += flower.getPrice();
        }
        for (Accessory accessory : accessories) {
            totalCost += accessory.getPrice();
        }
        return totalCost;
    }

    public int calculateTotalFreshness() {
        int totalFreshness = 0;
        for (Flower flower : flowers) {
            totalFreshness += flower.getFreshnessLevel();
        }
        return totalFreshness;
    }

    public int getBouquetId() {
        return bouquetId;
    }

    public void setBouquetId(int bouquetId) {
        this.bouquetId = bouquetId;
    }

    public void sortFlowersByFreshness() {
        Collections.sort(flowers, Comparator.comparingInt(Flower::getFreshnessLevel));
        logger.info("Квітки букета #" + bouquetId + " відсортовано за свіжістю.");
    }

    @Override
    public String toString() {
        return "Bouquet{" +
                "bouquetId=" + bouquetId +
                ", flowers=" + flowers +
                ", accessories=" + accessories +
                '}';
    }
}