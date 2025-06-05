package models;

import java.util.logging.Logger;
import logging.LoggerConfig;

public class Accessory {
    private final String name;
    private final double price;
    private static final Logger logger = LoggerConfig.getLogger();

    public Accessory(String name, double price) {
        this.name = name;
        this.price = price < 0 ? 0.0 : price;
        logger.info("Створено аксесуар: " + this);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Accessory{name='" + name + "', price=" + price + "}";
    }
}