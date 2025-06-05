package Command;

import models.Bouquet;
import models.Flower;
import models.Accessory;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class LoadBouquetsFromDatabaseMenu implements Command {
    private final List<Bouquet> bouquets;
    private final String dbUrl;
    private final Connection connection;
    private static final Logger logger = LoggerConfig.getLogger();

    public LoadBouquetsFromDatabaseMenu(List<Bouquet> bouquets) {
        this(bouquets, "jdbc:sqlite:flower.db", null);
    }

    public LoadBouquetsFromDatabaseMenu(List<Bouquet> bouquets, String dbUrl) {
        this(bouquets, dbUrl, null);
    }

    public LoadBouquetsFromDatabaseMenu(List<Bouquet> bouquets, String dbUrl, Connection connection) {
        this.bouquets = bouquets;
        this.dbUrl = dbUrl;
        this.connection = connection;
    }

    @Override
    public void execute() {
        Map<Integer, Bouquet> bouquetMap = new HashMap<>();
        Connection conn = null;
        try {
            conn = (connection != null) ? connection : DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement();

            ResultSet rsFlowers = stmt.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers");
            while (rsFlowers.next()) {
                int bouquetId = rsFlowers.getInt("bouquet_id");
                String flowerType = rsFlowers.getString("flower_type");
                double stemLength = rsFlowers.getDouble("stem_length");
                int freshnessLevel = rsFlowers.getInt("freshness_level");
                double price = rsFlowers.getDouble("price");

                if (freshnessLevel < 1 || freshnessLevel > 5) {
                    logger.warning("Невалідний рівень свіжості для квітки з ID " + bouquetId + ": " + freshnessLevel);
                    continue;
                }

                Flower flower = new Flower(flowerType, stemLength, freshnessLevel, price);

                Bouquet bouquet = bouquetMap.computeIfAbsent(bouquetId, id -> {
                    Bouquet b = new Bouquet(id);
                    boolean exists = bouquets.stream().anyMatch(existing -> existing.getBouquetId() == id);
                    if (!exists) {
                        bouquets.add(b);
                    }
                    logger.info("Завантажено букет #" + id);
                    return b;
                });

                bouquet.addFlower(flower);
                logger.info("  Квітка додана до букета з ID " + bouquetId + ": " + flower);
            }

            ResultSet rsAccessories = stmt.executeQuery("SELECT bouquet_id, name, price FROM accessories");
            while (rsAccessories.next()) {
                int bouquetId = rsAccessories.getInt("bouquet_id");
                String name = rsAccessories.getString("name");
                double price = rsAccessories.getDouble("price");

                if (price < 0) {
                    logger.warning("Невалідна ціна для аксесуара до букета з ID " + bouquetId + ": " + price);
                    continue;
                }

                Accessory accessory = new Accessory(name, price);

                Bouquet bouquet = bouquetMap.computeIfAbsent(bouquetId, id -> {
                    Bouquet b = new Bouquet(id);
                    bouquets.removeIf(existing -> existing.getBouquetId() == id);
                    bouquets.add(b);
                    logger.info("Завантажено букет #" + id);
                    return b;
                });

                bouquet.addAccessory(accessory);
                logger.info("  Аксесуар доданий до букета з ID " + bouquetId + ": " + accessory);
            }

            logger.info("Завантаження з бази SQLite завершено.");
        } catch (SQLException e) {
            logger.severe("Помилка при читанні з бази даних: " + e.getMessage());
            LoggerConfig.sendCriticalErrorEmail("Помилка завантаження з БД: " + e.getMessage());
        } finally {
            if (conn != null && connection == null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.severe("Помилка при закритті з’єднання: " + e.getMessage());
                }
            }
        }
    }
}
