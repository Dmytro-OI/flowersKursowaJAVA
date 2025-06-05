package gui;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxRobotException;
import org.hamcrest.Matchers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.util.stream.Collectors;

public class FlowerAppGuiTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new FlowerApp().start(stage);
    }

    @BeforeEach
    public void setUpEach() {
        interact(() -> {
            ComboBox<Integer> bouquetSelector = lookup(".combo-box").query();
            bouquetSelector.getItems().clear();
            ListView<String> bouquetListView = lookup(".list-view").query();
            bouquetListView.getItems().clear();
        });
        interact(() -> {
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:flower.db")) {
                java.sql.Statement stmt = conn.createStatement();
                stmt.execute("DELETE FROM flowers");
                stmt.execute("DELETE FROM accessories");
            } catch (java.sql.SQLException e) {
                System.err.println("Помилка очищення БД: " + e.getMessage());
            }
        });
    }

    private ComboBox<Integer> getBouquetSelector() {
        return lookup(".combo-box").query();
    }

    private ListView<String> getBouquetListView() {
        return lookup(".list-view").query();
    }

    @Test
    public void testAddBouquet_ValidAndInvalid() {
        clickOn("➕ Додати букет");
        write("1");
        clickOn("OK");
        sleep(500);

        verifyThat(".combo-box", combo -> ((ComboBox<?>) combo).getItems().contains(1));

        clickOn("➕ Додати букет");
        write("1");
        clickOn("OK");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Некоректний ID букета (ID уже існує в пам’яті)."));
        clickOn(".dialog-pane .button");

        clickOn("➕ Додати букет");
        write("0");
        clickOn("OK");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Некоректний ID букета (ID має бути > 0)."));
        clickOn(".dialog-pane .button");

        clickOn("➕ Додати букет");
        write("-1");
        clickOn("OK");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Некоректний ID букета (ID має бути > 0)."));
        clickOn(".dialog-pane .button");
    }

    @Test
    public void testAddFlower_ValidAndInvalidFreshness() {
        clickOn("➕ Додати букет");
        write("2");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Троянда");
        clickOn("OK");
        write("30");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("25");
        clickOn("OK");
        sleep(500);

        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().anyMatch(s -> s.contains("Троянда"))
        );

        clickOn("➕ Додати квітку");
        write("Тюльпан");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("6");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        verifyThat(".dialog-pane .content.label", hasText("Рівень свіжості має бути від 1 до 5!"));
        clickOn(".dialog-pane .button");

        clickOn("➕ Додати квітку");
        write("Тюльпан");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("0");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        verifyThat(".dialog-pane .content.label", hasText("Рівень свіжості має бути від 1 до 5!"));
        clickOn(".dialog-pane .button");
    }

    @Test
    public void testSortFlowers() {
        clickOn("➕ Додати букет");
        write("5");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("5");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Тюльпан");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("3");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        clickOn("🔃 Сортувати квіти");
        sleep(500);

        verifyThat(getBouquetListView(), list -> {
            String firstFlowerLine = list.getItems().stream()
                    .filter(s -> s.contains("Тип:"))
                    .findFirst().orElse("");
            return firstFlowerLine.contains("Тюльпан");
        });

        clickOn("❌ Видалити квітку (за індексом)");
        write("1");
        clickOn("OK");
        sleep(500);
        clickOn("❌ Видалити квітку (за індексом)");
        write("1");
        clickOn("OK");
        sleep(500);
        clickOn("🔃 Сортувати квіти");
    }

    @Test
    public void testCalculateTotalCostAndFreshness() {
        clickOn("➕ Додати букет");
        write("6");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("🎀 Додати аксесуар");
        write("Стрічка");
        clickOn("OK");
        write("5");
        clickOn("OK");
        sleep(500);

        clickOn("💰 Порахувати вартість");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Загальна вартість букета: 25.0 грн"));
        clickOn(".dialog-pane .button");

        clickOn("🌸 Порахувати свіжість");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Загальна свіжість букета: 4 (середнє: 4)"));
        clickOn(".dialog-pane .button");

        clickOn("❌ Видалити квітку (за індексом)");
        write("1");
        clickOn("OK");
        sleep(500);
        clickOn("🌸 Порахувати свіжість");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Загальна свіжість букета: 0 (середнє: 0)"));
        clickOn(".dialog-pane .button");
    }

    @Test
    public void testFilterFlowers() {
        clickOn("➕ Додати букет");
        write("7");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("10");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Тюльпан");
        clickOn("OK");
        write("30");
        clickOn("OK");
        write("3");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        clickOn("🔍 Фільтр за довжиною стебла");
        write("5");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        verifyThat(".dialog-pane .content.label", hasText(Matchers.containsString("Роза")));
        clickOn(".dialog-pane .button");

        clickOn("🔍 Фільтр за довжиною стебла");
        write("100");
        clickOn("OK");
        write("150");
        clickOn("OK");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Квітки не знайдено."));
        clickOn(".dialog-pane .button");
    }

    @Test
    public void testSaveAndLoadFromDatabase() {
        clickOn("➕ Додати букет");
        write("8");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Дані збережено у базу даних."));
        clickOn(".dialog-pane .button");

        clickOn("❌ Видалити букет");

        clickOn("📤 Завантажити з бази");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Дані завантажено з бази даних."));
        clickOn(".dialog-pane .button");

        verifyThat(".combo-box", combo -> ((ComboBox<?>) combo).getItems().contains(8));

        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().anyMatch(s -> s.contains("Роза"))
        );
    }

    @Test
    public void testDeleteBouquetFromDatabase() {
        clickOn("➕ Додати букет");
        write("9");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        clickOn(".dialog-pane .button");

        clickOn("🗑 Видалити букет з БД");
        sleep(500);
        verifyThat(".dialog-pane .content.label", hasText("Букет #9 видалено з бази даних."));
        clickOn(".dialog-pane .button");

        verifyThat(".combo-box", combo -> !((ComboBox<?>) combo).getItems().contains(9));

        clickOn("📤 Завантажити з бази");
        sleep(500);
        clickOn(".dialog-pane .button");
        verifyThat(".combo-box", combo -> !((ComboBox<?>) combo).getItems().contains(9));
    }

    @Test
    public void testAddBouquetWithExistingIdInDatabase() {
        clickOn("➕ Додати букет");
        write("10");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        clickOn(".dialog-pane .button");

        clickOn("❌ Видалити букет");

        clickOn("➕ Додати букет");
        write("10");
        clickOn("OK");
        sleep(500);

        verifyThat(".dialog-pane .content.label", hasText("Букет із ID 10 уже існує в БД. Ви можете доповнити його."));
        clickOn(".dialog-pane .button");

        verifyThat(".combo-box", combo -> ((ComboBox<?>) combo).getItems().contains(10));

        clickOn("➕ Додати квітку");
        write("Тюльпан");
        clickOn("OK");
        write("30");
        clickOn("OK");
        write("3");
        clickOn("OK");
        write("15");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        clickOn(".dialog-pane .button");

        clickOn("❌ Видалити букет");

        clickOn("📤 Завантажити з бази");
        sleep(500);
        clickOn(".dialog-pane .button");

        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().anyMatch(s -> s.contains("Роза"))
        );
        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().anyMatch(s -> s.contains("Тюльпан"))
        );
    }

    @Test
    public void testAddDuplicateFlowerAndAccessoryToDatabase() {
        clickOn("➕ Додати букет");
        write("11");
        clickOn("OK");
        sleep(500);

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        sleep(500);

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("🎀 Додати аксесуар");
        write("Стрічка");
        clickOn("OK");
        write("5");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        clickOn(".dialog-pane .button");

        clickOn("➕ Додати квітку");
        write("Роза");
        clickOn("OK");
        write("20");
        clickOn("OK");
        write("4");
        clickOn("OK");
        write("20");
        clickOn("OK");
        sleep(500);

        clickOn("🎀 Додати аксесуар");
        write("Стрічка");
        clickOn("OK");
        write("5");
        clickOn("OK");
        sleep(500);

        clickOn("📥 Зберегти в базу");
        sleep(500);
        clickOn(".dialog-pane .button");

        clickOn("❌ Видалити букет");

        clickOn("📤 Завантажити з бази");
        sleep(500);
        clickOn(".dialog-pane .button");

        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().filter(s -> s.contains("Роза")).count() == 1);
        verifyThat(getBouquetListView(), list ->
                list.getItems().stream().filter(s -> s.contains("Стрічка")).count() == 1);
    }

    @Test
    public void testActionsWithoutSelectedBouquet() {
        interact(() -> {
            System.out.println("Доступні кнопки у UI:");
            lookup("Button").queryAll().forEach(button ->
                    System.out.println(((Button) button).getText())
            );
        });

        sleep(1000);

        clickOn("➕ Додати квітку");
        sleep(500);
        verifyThat(getBouquetListView(), list ->
                list.getItems().isEmpty() || list.getItems().get(0).contains("немає квіток"));

        clickOn("🎀 Додати аксесуар");
        sleep(500);

        clickOn("🔃 Сортувати квіти");
        sleep(500);

        clickOn("💰 Порахувати вартість");
        sleep(500);

        try {
            clickOn("🌸 Порахувати свіжість");
        } catch (Exception e) {
            System.err.println("Кнопка '🌸 Порахувати свіжість' не знайдена. Перевірте FlowerApp.java.");
            throw e;
        }
        sleep(500);

        clickOn("🔍 Фільтр за довжиною стебла");
        sleep(500);

        clickOn("❌ Видалити букет");
        sleep(500);

        clickOn("➕ Додати букет");
        write("12");
        clickOn("OK");
        sleep(500);
        verifyThat(".combo-box", combo -> ((ComboBox<?>) combo).getItems().contains(12));
    }
}