package Command;

import models.Bouquet;
import models.Flower;
import models.Accessory;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;
import logging.LoggerConfig;

public class SaveBouquetsToDatabaseMenu implements Command {
    private final List<Bouquet> bouquets;
    private final String dbUrl;
    private static final Logger logger = LoggerConfig.getLogger();

    public SaveBouquetsToDatabaseMenu(List<Bouquet> bouquets) {
        this(bouquets, "jdbc:sqlite:flower.db");
    }

    public SaveBouquetsToDatabaseMenu(List<Bouquet> bouquets, String dbUrl) {
        this.bouquets = bouquets;
        this.dbUrl = dbUrl;
    }

    @Override
    public void execute() {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS flowers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL, " +
                    "flower_type TEXT NOT NULL, " +
                    "stem_length REAL NOT NULL, " +
                    "freshness_level INTEGER NOT NULL, " +
                    "price REAL NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS accessories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL)");

            String flowerSql = "INSERT INTO flowers (bouquet_id, flower_type, stem_length, freshness_level, price) VALUES (?, ?, ?, ?, ?)";
            String accessorySql = "INSERT INTO accessories (bouquet_id, name, price) VALUES (?, ?, ?)";
            String checkFlowerSql = "SELECT COUNT(*) FROM flowers WHERE bouquet_id = ? AND flower_type = ? AND stem_length = ? AND freshness_level = ? AND price = ?";
            String checkAccessorySql = "SELECT COUNT(*) FROM accessories WHERE bouquet_id = ? AND name = ? AND price = ?";

            try (PreparedStatement pstmtFlowers = conn.prepareStatement(flowerSql);
                 PreparedStatement pstmtAccessories = conn.prepareStatement(accessorySql);
                 PreparedStatement checkFlowerStmt = conn.prepareStatement(checkFlowerSql);
                 PreparedStatement checkAccessoryStmt = conn.prepareStatement(checkAccessorySql)) {

                for (Bouquet bouquet : bouquets) {
                    for (Flower flower : bouquet.getFlowers()) {
                        checkFlowerStmt.setInt(1, bouquet.getBouquetId());
                        checkFlowerStmt.setString(2, flower.getFlowerType());
                        checkFlowerStmt.setDouble(3, flower.getStemLength());
                        checkFlowerStmt.setInt(4, flower.getFreshnessLevel());
                        checkFlowerStmt.setDouble(5, flower.getPrice());
                        ResultSet rs = checkFlowerStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) == 0) {
                            pstmtFlowers.setInt(1, bouquet.getBouquetId());
                            pstmtFlowers.setString(2, flower.getFlowerType());
                            pstmtFlowers.setDouble(3, flower.getStemLength());
                            pstmtFlowers.setInt(4, flower.getFreshnessLevel());
                            pstmtFlowers.setDouble(5, flower.getPrice());
                            pstmtFlowers.executeUpdate();
                            logger.info("Записано у БД: Букет " + bouquet.getBouquetId() + ", Квітка: " + flower);
                        } else {
                            logger.info("Квітка вже існує у БД: " + flower);
                        }
                    }

                    for (Accessory accessory : bouquet.getAccessories()) {
                        checkAccessoryStmt.setInt(1, bouquet.getBouquetId());
                        checkAccessoryStmt.setString(2, accessory.getName());
                        checkAccessoryStmt.setDouble(3, accessory.getPrice());
                        ResultSet rs = checkAccessoryStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) == 0) {
                            pstmtAccessories.setInt(1, bouquet.getBouquetId());
                            pstmtAccessories.setString(2, accessory.getName());
                            pstmtAccessories.setDouble(3, accessory.getPrice());
                            pstmtAccessories.executeUpdate();
                            logger.info("Записано у БД: Букет " + bouquet.getBouquetId() + ", Аксесуар: " + accessory);
                        } else {
                            logger.info("Аксесуар вже існує у БД: " + accessory);
                        }
                    }
                }
            }

            logger.info("Збереження у базу SQLite завершено успішно.");
        } catch (SQLException e) {
            logger.severe("Помилка SQL: " + e.getMessage());
            LoggerConfig.sendCriticalErrorEmail("Помилка збереження у БД: " + e.getMessage());
        }
    }

    public static void deleteBouquetFromDatabase(int bouquetId, String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            try (PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM flowers WHERE bouquet_id = ?")) {
                pstmt1.setInt(1, bouquetId);
                pstmt1.executeUpdate();
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM accessories WHERE bouquet_id = ?")) {
                pstmt2.setInt(1, bouquetId);
                pstmt2.executeUpdate();
            }
            logger.info("Видалено букет з ID: " + bouquetId);
        } catch (SQLException e) {
            logger.severe("Помилка SQL (deleteBouquet): " + e.getMessage());
        }
    }

    public static void deleteBouquetFromDatabase(int bouquetId) {
        deleteBouquetFromDatabase(bouquetId, "jdbc:sqlite:flower.db");
    }

    public static void deleteFlowerFromDatabase(int bouquetId, int index, String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            String selectSql = "SELECT id FROM flowers WHERE bouquet_id = ? ORDER BY id LIMIT 1 OFFSET ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, bouquetId);
                selectStmt.setInt(2, index);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int flowerId = rs.getInt("id");
                    String deleteSql = "DELETE FROM flowers WHERE id = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, flowerId);
                        deleteStmt.executeUpdate();
                        logger.info("Видалено квітку з ID: " + flowerId + " (букет #" + bouquetId + ")");
                    }
                } else {
                    logger.warning("Квітку не знайдено для букета #" + bouquetId + " з індексом: " + index);
                }
            }
        } catch (SQLException e) {
            logger.severe("Помилка SQL (deleteFlower): " + e.getMessage());
        }
    }

    public static void deleteFlowerFromDatabase(int bouquetId, int index) {
        deleteFlowerFromDatabase(bouquetId, index, "jdbc:sqlite:flower.db");
    }

    public static void deleteAccessoryFromDatabase(int bouquetId, int index, String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            String selectSql = "SELECT id FROM accessories WHERE bouquet_id = ? ORDER BY id LIMIT 1 OFFSET ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, bouquetId);
                selectStmt.setInt(2, index);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int accessoryId = rs.getInt("id");
                    String deleteSql = "DELETE FROM accessories WHERE id = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, accessoryId);
                        deleteStmt.executeUpdate();
                        logger.info("Видалено аксесуар з ID: " + accessoryId + " (букет #" + bouquetId + ")");
                    }
                } else {
                    logger.warning("Аксесуар не знайдено для букета #" + bouquetId + " з індексом: " + index);
                }
            }
        } catch (SQLException e) {
            logger.severe("Помилка SQL (deleteAccessory): " + e.getMessage());
        }
    }

    public static void deleteAccessoryFromDatabase(int bouquetId, int index) {
        deleteAccessoryFromDatabase(bouquetId, index, "jdbc:sqlite:flower.db");
    }

    public static boolean bouquetExistsInDatabase(int bouquetId, String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            String sql = "SELECT COUNT(*) FROM flowers WHERE bouquet_id = ? UNION ALL SELECT COUNT(*) FROM accessories WHERE bouquet_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bouquetId);
                stmt.setInt(2, bouquetId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt(1) > 0) return true;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Помилка перевірки існування букета: " + e.getMessage());
            return false;
        }
    }
}