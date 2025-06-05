package Command;

import models.Bouquet;
import models.Flower;
import models.Accessory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoadBouquetsFromDatabaseMenuTest {

    private List<Bouquet> bouquets;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet flowersResultSet;

    @Mock
    private ResultSet accessoriesResultSet;

    @InjectMocks
    private LoadBouquetsFromDatabaseMenu command;

    @BeforeEach
    public void setUp() throws SQLException {
        bouquets = new ArrayList<>();
        command = new LoadBouquetsFromDatabaseMenu(bouquets, "jdbc:sqlite:flower_test.db", connection);
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    public void testExecute_LoadsDataCorrectly() throws SQLException {
        when(statement.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers"))
                .thenReturn(flowersResultSet);
        when(statement.executeQuery("SELECT bouquet_id, name, price FROM accessories"))
                .thenReturn(accessoriesResultSet);

        when(flowersResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(flowersResultSet.getInt("bouquet_id"))
                .thenReturn(1)
                .thenReturn(1);
        when(flowersResultSet.getString("flower_type"))
                .thenReturn("Rose")
                .thenReturn("Tulip");
        when(flowersResultSet.getDouble("stem_length"))
                .thenReturn(30.0)
                .thenReturn(20.0);
        when(flowersResultSet.getInt("freshness_level"))
                .thenReturn(4)
                .thenReturn(3);
        when(flowersResultSet.getDouble("price"))
                .thenReturn(25.0)
                .thenReturn(15.0);
        when(accessoriesResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(accessoriesResultSet.getInt("bouquet_id"))
                .thenReturn(1);
        when(accessoriesResultSet.getString("name"))
                .thenReturn("Ribbon");
        when(accessoriesResultSet.getDouble("price"))
                .thenReturn(10.0);

        command.execute();

        assertEquals(1, bouquets.size(), "Only 1 bouquet should be loaded.");
        Bouquet bouquet = bouquets.get(0);
        assertEquals(2, bouquet.getFlowers().size(), "Bouquet should contain 2 flowers.");
        assertEquals(1, bouquet.getAccessories().size(), "Bouquet should contain 1 accessory.");
        assertEquals("Rose", bouquet.getFlowers().get(0).getFlowerType());
        assertEquals("Ribbon", bouquet.getAccessories().get(0).getName());
    }

    @Test
    public void testExecute_SkipsInvalidFreshnessLevel() throws SQLException {
        when(statement.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers"))
                .thenReturn(flowersResultSet);
        when(statement.executeQuery("SELECT bouquet_id, name, price FROM accessories"))
                .thenReturn(accessoriesResultSet);

        when(flowersResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(flowersResultSet.getInt("bouquet_id"))
                .thenReturn(1);
        when(flowersResultSet.getString("flower_type"))
                .thenReturn("Rose");
        when(flowersResultSet.getDouble("stem_length"))
                .thenReturn(30.0);
        when(flowersResultSet.getInt("freshness_level"))
                .thenReturn(0);
        when(flowersResultSet.getDouble("price"))
                .thenReturn(25.0);

        when(accessoriesResultSet.next())
                .thenReturn(false);

        command.execute();

        assertTrue(bouquets.isEmpty(), "No bouquets should be added with invalid freshness level.");
    }

    @Test
    public void testExecute_SkipsInvalidAccessoryPrice() throws SQLException {
        when(statement.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers"))
                .thenReturn(flowersResultSet);
        when(statement.executeQuery("SELECT bouquet_id, name, price FROM accessories"))
                .thenReturn(accessoriesResultSet);

        when(flowersResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(flowersResultSet.getInt("bouquet_id"))
                .thenReturn(1);
        when(flowersResultSet.getString("flower_type"))
                .thenReturn("Rose");
        when(flowersResultSet.getDouble("stem_length"))
                .thenReturn(30.0);
        when(flowersResultSet.getInt("freshness_level"))
                .thenReturn(4);
        when(flowersResultSet.getDouble("price"))
                .thenReturn(25.0);

        when(accessoriesResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(accessoriesResultSet.getInt("bouquet_id"))
                .thenReturn(1);
        when(accessoriesResultSet.getString("name"))
                .thenReturn("Ribbon");
        when(accessoriesResultSet.getDouble("price"))
                .thenReturn(-10.0);

        command.execute();

        Bouquet bouquet = bouquets.get(0);
        assertTrue(bouquet.getAccessories().isEmpty(), "No accessories should be added with invalid price.");
    }

    @Test
    public void testExecute_DatabaseError() throws SQLException {
        when(statement.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers"))
                .thenThrow(new SQLException("Database error"));

        command.execute();

        assertTrue(bouquets.isEmpty(), "No bouquets should be loaded if database throws an error.");
    }

    @Test
    public void testExecute_SkipsDuplicateBouquets() throws SQLException {
        when(statement.executeQuery("SELECT bouquet_id, flower_type, stem_length, freshness_level, price FROM flowers"))
                .thenReturn(flowersResultSet);
        when(statement.executeQuery("SELECT bouquet_id, name, price FROM accessories"))
                .thenReturn(accessoriesResultSet);

        when(flowersResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(flowersResultSet.getInt("bouquet_id"))
                .thenReturn(1)
                .thenReturn(1);
        when(flowersResultSet.getString("flower_type"))
                .thenReturn("Rose")
                .thenReturn("Tulip");
        when(flowersResultSet.getDouble("stem_length"))
                .thenReturn(30.0)
                .thenReturn(20.0);
        when(flowersResultSet.getInt("freshness_level"))
                .thenReturn(4)
                .thenReturn(3);
        when(flowersResultSet.getDouble("price"))
                .thenReturn(25.0)
                .thenReturn(15.0);

        when(accessoriesResultSet.next())
                .thenReturn(true)
                .thenReturn(false);
        when(accessoriesResultSet.getInt("bouquet_id"))
                .thenReturn(1);
        when(accessoriesResultSet.getString("name"))
                .thenReturn("Ribbon");
        when(accessoriesResultSet.getDouble("price"))
                .thenReturn(10.0);

        command.execute();
        command.execute();

        assertEquals(1, bouquets.size(), "Duplicate bouquets should not be added.");
    }
}
