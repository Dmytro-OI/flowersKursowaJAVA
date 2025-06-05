package Command;

import models.Bouquet;
import models.Flower;
import models.Accessory;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveBouquetsToDatabaseMenuTest {

    private static final String DB_URL = "jdbc:sqlite:test_flower.db";

    @BeforeEach
    public void setupDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS flowers");
            stmt.execute("DROP TABLE IF EXISTS accessories");

            stmt.execute("CREATE TABLE flowers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL, " +
                    "flower_type TEXT NOT NULL, " +
                    "stem_length REAL NOT NULL, " +
                    "freshness_level INTEGER NOT NULL, " +
                    "price REAL NOT NULL)");

            stmt.execute("CREATE TABLE accessories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bouquet_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL)");
        }
    }

    @AfterEach
    public void cleanupDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS flowers");
            stmt.execute("DROP TABLE IF EXISTS accessories");
        }
    }

    @Test
    public void testExecute_SavesDataCorrectly() throws Exception {
        List<Bouquet> bouquets = new ArrayList<>();

        Bouquet bouquet = new Bouquet(1);
        bouquet.addFlower(new Flower("Rose", 30.5, 5, 15.0));
        bouquet.addFlower(new Flower("Tulip", 25.0, 4, 10.0));
        bouquet.addAccessory(new Accessory("Ribbon", 2.5));
        bouquet.addAccessory(new Accessory("Card", 1.0));
        bouquets.add(bouquet);

        SaveBouquetsToDatabaseMenu command = new SaveBouquetsToDatabaseMenu(bouquets, DB_URL);
        command.execute();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM flowers WHERE bouquet_id = ?")) {
            pstmt.setInt(1, 1);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM accessories WHERE bouquet_id = ?")) {
            pstmt.setInt(1, 1);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    public void testDeleteBouquetFromDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtFlower = conn.prepareStatement(
                     "INSERT INTO flowers (bouquet_id, flower_type, stem_length, freshness_level, price) VALUES (?, ?, ?, ?, ?)");
             PreparedStatement pstmtAccessory = conn.prepareStatement(
                     "INSERT INTO accessories (bouquet_id, name, price) VALUES (?, ?, ?)")) {
            pstmtFlower.setInt(1, 3);
            pstmtFlower.setString(2, "Lily");
            pstmtFlower.setDouble(3, 20.0);
            pstmtFlower.setInt(4, 3);
            pstmtFlower.setDouble(5, 12.0);
            pstmtFlower.executeUpdate();

            pstmtAccessory.setInt(1, 3);
            pstmtAccessory.setString(2, "Wrap");
            pstmtAccessory.setDouble(3, 3.0);
            pstmtAccessory.executeUpdate();
        }

        SaveBouquetsToDatabaseMenu.deleteBouquetFromDatabase(3, DB_URL);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtF = conn.prepareStatement("SELECT COUNT(*) FROM flowers WHERE bouquet_id = ?");
             PreparedStatement pstmtA = conn.prepareStatement("SELECT COUNT(*) FROM accessories WHERE bouquet_id = ?")) {
            pstmtF.setInt(1, 3);
            ResultSet rsF = pstmtF.executeQuery();
            assertTrue(rsF.next());
            assertEquals(0, rsF.getInt(1));

            pstmtA.setInt(1, 3);
            ResultSet rsA = pstmtA.executeQuery();
            assertTrue(rsA.next());
            assertEquals(0, rsA.getInt(1));
        }
    }

    @Test
    public void testDeleteBouquetFromDatabase_WithoutDbUrl() {
        assertDoesNotThrow(() -> SaveBouquetsToDatabaseMenu.deleteBouquetFromDatabase(999));
    }

    @Test
    public void testDeleteFlowerFromDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO flowers (bouquet_id, flower_type, stem_length, freshness_level, price) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, 4);
            pstmt.setString(2, "Daisy");
            pstmt.setDouble(3, 15.0);
            pstmt.setInt(4, 2);
            pstmt.setDouble(5, 7.0);
            pstmt.executeUpdate();

            pstmt.setInt(1, 4);
            pstmt.setString(2, "Orchid");
            pstmt.setDouble(3, 18.0);
            pstmt.setInt(4, 3);
            pstmt.setDouble(5, 12.0);
            pstmt.executeUpdate();
        }

        SaveBouquetsToDatabaseMenu.deleteFlowerFromDatabase(4, 0, DB_URL);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM flowers WHERE bouquet_id = ?")) {
            pstmt.setInt(1, 4);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    public void testDeleteFlowerFromDatabase_WithoutDbUrl() {
        assertDoesNotThrow(() -> SaveBouquetsToDatabaseMenu.deleteFlowerFromDatabase(99, 100));
    }

    @Test
    public void testDeleteAccessoryFromDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO accessories (bouquet_id, name, price) VALUES (?, ?, ?)")) {
            pstmt.setInt(1, 5);
            pstmt.setString(2, "Box");
            pstmt.setDouble(3, 5.0);
            pstmt.executeUpdate();

            pstmt.setInt(1, 5);
            pstmt.setString(2, "Card");
            pstmt.setDouble(3, 3.0);
            pstmt.executeUpdate();
        }

        SaveBouquetsToDatabaseMenu.deleteAccessoryFromDatabase(5, 0, DB_URL);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM accessories WHERE bouquet_id = ?")) {
            pstmt.setInt(1, 5);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    public void testDeleteAccessoryFromDatabase_WithoutDbUrl() {
        assertDoesNotThrow(() -> SaveBouquetsToDatabaseMenu.deleteAccessoryFromDatabase(99, 100));
    }

    @Test
    public void testExecute_WithEmptyList() {
        List<Bouquet> bouquets = new ArrayList<>();
        SaveBouquetsToDatabaseMenu command = new SaveBouquetsToDatabaseMenu(bouquets, DB_URL);
        assertDoesNotThrow(command::execute);
    }
}
