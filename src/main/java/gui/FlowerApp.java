package gui;

import Command.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Bouquet;
import models.Flower;
import models.Accessory;

import java.util.*;

public class FlowerApp extends Application {
    private final List<Bouquet> bouquets = new ArrayList<>();
    private final ListView<String> bouquetListView = new ListView<>();
    private final ComboBox<Integer> bouquetSelector = new ComboBox<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Керування букетами квітів");

        Button addBouquetBtn = new Button("➕ Додати букет");
        Button deleteBouquetBtn = new Button("❌ Видалити букет");
        Button addFlowerBtn = new Button("➕ Додати квітку");
        Button addAccessoryBtn = new Button("🎀 Додати аксесуар");
        Button removeFlowerBtn = new Button("❌ Видалити квітку (за індексом)");
        Button removeAccessoryBtn = new Button("❌ Видалити аксесуар (за індексом)");
        Button sortBtn = new Button("🔃 Сортувати квіти");
        Button totalCostBtn = new Button("💰 Порахувати вартість");
        Button totalFreshnessBtn = new Button("🌸 Порахувати свіжість");
        Button filterBtn = new Button("🔍 Фільтр за довжиною стебла");
        Button saveDbBtn = new Button("📥 Зберегти в базу");
        Button loadDbBtn = new Button("📤 Завантажити з бази");
        Button deleteBouquetDbBtn = new Button("🗑 Видалити букет з БД");

        FlowPane buttons = new FlowPane(10, 10,
                addBouquetBtn, deleteBouquetBtn, addFlowerBtn, addAccessoryBtn,
                removeFlowerBtn, removeAccessoryBtn, sortBtn, totalCostBtn, totalFreshnessBtn,
                filterBtn, saveDbBtn, loadDbBtn, deleteBouquetDbBtn);
        buttons.setPadding(new Insets(10));

        VBox layout = new VBox(10,
                new Label("Виберіть букет:"),
                bouquetSelector,
                bouquetListView,
                buttons);
        layout.setPadding(new Insets(15));

        bouquetSelector.setOnAction(e -> refreshBouquetDisplay());

        addBouquetBtn.setOnAction(e -> {
            int bouquetId = promptInt("Введіть ID нового букета:", bouquets.size() + 1);
            if (bouquetId <= 0) {
                showInfo("Некоректний ID букета (ID має бути > 0).");
                return;
            }
            if (SaveBouquetsToDatabaseMenu.bouquetExistsInDatabase(bouquetId, "jdbc:sqlite:flower.db")) {
                if (!bouquetSelector.getItems().contains(bouquetId)) {
                    Bouquet existingBouquet = new Bouquet(bouquetId);
                    bouquets.add(existingBouquet);
                    new LoadBouquetsFromDatabaseMenu(bouquets).execute();
                    bouquetSelector.getItems().add(bouquetId);
                    bouquetSelector.setValue(bouquetId);
                    refreshBouquetDisplay();
                    showInfo("Букет із ID " + bouquetId + " уже існує в БД. Ви можете доповнити його.");
                } else {
                    showInfo("Букет із ID " + bouquetId + " уже існує в пам’яті. Ви можете доповнити його.");
                    bouquetSelector.setValue(bouquetId);
                }
            } else {
                if (bouquetSelector.getItems().contains(bouquetId)) {
                    showInfo("Некоректний ID букета (ID уже існує в пам’яті).");
                    return;
                }
                Command cmd = new AddBouquetMenu(bouquets, bouquetId);
                cmd.execute();
                bouquetSelector.getItems().add(bouquetId);
                bouquetSelector.setValue(bouquetId);
                refreshBouquetDisplay();
            }
        });

        deleteBouquetBtn.setOnAction(e -> {
            Integer selected = bouquetSelector.getValue();
            if (selected != null) {
                Command cmd = new DeleteBouquetMenu(bouquets, selected);
                cmd.execute();
                bouquetSelector.getItems().remove(selected);
                bouquetSelector.getSelectionModel().clearSelection();
                refreshBouquetDisplay();
            }
        });

        addFlowerBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                String type = promptString("Тип квітки (наприклад, Троянда):", "Троянда");
                double stem = promptDouble("Довжина стебла (см):", 30.0);
                int freshness = promptInt("Рівень свіжості (1-5):", 3);
                double price = promptDouble("Ціна (грн):", 25.0);
                if (freshness < 1 || freshness > 5) {
                    showInfo("Рівень свіжості має бути від 1 до 5!");
                    return;
                }
                Command cmd = new AddFlowerMenu(bouquet, type, stem, freshness, price);
                cmd.execute();
                refreshBouquetDisplay();
            }
        });

        addAccessoryBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                String name = promptString("Назва аксесуара (наприклад, Стрічка):", "Стрічка");
                double price = promptDouble("Ціна аксесуара (грн):", 10.0);
                Command cmd = new AddAccessoryMenu(bouquet, name, price);
                cmd.execute();
                refreshBouquetDisplay();
            }
        });

        removeFlowerBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null && !bouquet.getFlowers().isEmpty()) {
                int index = promptInt("Введіть індекс квітки (з 1):", 1) - 1;
                if (index >= 0 && index < bouquet.getFlowers().size()) {
                    Command cmd = new RemoveFlowerMenu(bouquet, index);
                    cmd.execute();
                    SaveBouquetsToDatabaseMenu.deleteFlowerFromDatabase(bouquet.getBouquetId(), index);
                    refreshBouquetDisplay();
                } else {
                    showInfo("Некоректний індекс квітки");
                }
            } else {
                showInfo("Букет порожній або не вибраний!");
            }
        });

        removeAccessoryBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null && !bouquet.getAccessories().isEmpty()) {
                int index = promptInt("Введіть номер аксесуара (з 1):", 1) - 1;
                if (index >= 0 && index < bouquet.getAccessories().size()) {
                    Accessory toRemove = bouquet.getAccessories().get(index);
                    bouquet.removeAccessory(toRemove);
                    SaveBouquetsToDatabaseMenu.deleteAccessoryFromDatabase(bouquet.getBouquetId(), index);
                    refreshBouquetDisplay();
                } else {
                    showInfo("Некоректний номер аксесуара");
                }
            }
        });

        sortBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                Command cmd = new SortFlowersMenu(bouquet);
                cmd.execute();
                refreshBouquetDisplay();
            }
        });

        totalCostBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                Command cmd = new CalculateTotalCostMenu(bouquet);
                cmd.execute();
                showInfo("Загальна вартість букета: " + bouquet.calculateTotalCost() + " грн");
            }
        });

        totalFreshnessBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                int totalFreshness = bouquet.calculateTotalFreshness();
                showInfo("Загальна свіжість букета: " + totalFreshness + " (середнє: " + (totalFreshness / Math.max(1, bouquet.getFlowers().size())) + ")");
            }
        });

        filterBtn.setOnAction(e -> {
            Bouquet bouquet = getSelectedBouquet();
            if (bouquet != null) {
                double min = promptDouble("Мінімальна довжина стебла (см):", 10.0);
                double max = promptDouble("Максимальна довжина стебла (см):", 50.0);
                FindFlowersMenu cmd = new FindFlowersMenu(bouquet, min, max);
                cmd.execute();
                List<Flower> found = cmd.getMatchedFlowers();
                if (found.isEmpty()) {
                    showInfo("Квітки не знайдено.");
                } else {
                    StringBuilder sb = new StringBuilder("Знайдено квітки:\n");
                    for (Flower f : found) sb.append(f).append("\n");
                    showInfo(sb.toString());
                }
            }
        });

        saveDbBtn.setOnAction(e -> {
            new SaveBouquetsToDatabaseMenu(bouquets).execute();
            showInfo("Дані збережено у базу даних.");
        });

        loadDbBtn.setOnAction(e -> {
            new LoadBouquetsFromDatabaseMenu(bouquets).execute();
            updateBouquetSelector();
            showInfo("Дані завантажено з бази даних.");
        });

        deleteBouquetDbBtn.setOnAction(e -> {
            Integer selected = bouquetSelector.getValue();
            if (selected != null) {
                SaveBouquetsToDatabaseMenu.deleteBouquetFromDatabase(selected);
                bouquets.removeIf(b -> b.getBouquetId() == selected);
                updateBouquetSelector();
                showInfo("Букет #" + selected + " видалено з бази даних.");
            }
        });

        stage.setScene(new Scene(layout, 1150, 600));
        stage.show();
    }

    private void updateBouquetSelector() {
        bouquetSelector.getItems().clear();
        Set<Integer> seen = new HashSet<>();
        for (Bouquet b : bouquets) {
            if (!seen.contains(b.getBouquetId())) {
                bouquetSelector.getItems().add(b.getBouquetId());
                seen.add(b.getBouquetId());
            }
        }
        if (!bouquets.isEmpty()) bouquetSelector.setValue(bouquets.get(0).getBouquetId());
        refreshBouquetDisplay();
    }

    private Bouquet getSelectedBouquet() {
        Integer selected = bouquetSelector.getValue();
        if (selected == null) return null;
        return bouquets.stream().filter(b -> b.getBouquetId() == selected).findFirst().orElse(null);
    }

    private void refreshBouquetDisplay() {
        bouquetListView.getItems().clear();
        Bouquet bouquet = getSelectedBouquet();
        if (bouquet != null) {
            bouquetListView.getItems().add("💐 Букет #" + bouquet.getBouquetId());
            List<Flower> flowers = bouquet.getFlowers();
            if (flowers.isEmpty()) {
                bouquetListView.getItems().add("  (немає квіток)");
            } else {
                for (int i = 0; i < flowers.size(); i++) {
                    Flower f = flowers.get(i);
                    bouquetListView.getItems().add("  " + (i + 1) + ". " +
                            "Тип: " + f.getFlowerType() +
                            ", Довжина стебла: " + f.getStemLength() +
                            " см, Свіжість: " + f.getFreshnessLevel() +
                            ", Ціна: " + f.getPrice() + " грн");
                }
            }
            List<Accessory> accessories = bouquet.getAccessories();
            if (!accessories.isEmpty()) {
                bouquetListView.getItems().add("  Аксесуари:");
                for (int i = 0; i < accessories.size(); i++) {
                    Accessory a = accessories.get(i);
                    bouquetListView.getItems().add("    " + (i + 1) + ". " +
                            "Назва: " + a.getName() +
                            ", Ціна: " + a.getPrice() + " грн");
                }
            }
        }
    }

    private int promptInt(String msg, int def) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(def));
        dialog.setTitle("Ввід");
        dialog.setHeaderText(null);
        dialog.setContentText(msg);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(def);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private double promptDouble(String msg, double def) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(def));
        dialog.setTitle("Ввід");
        dialog.setHeaderText(null);
        dialog.setContentText(msg);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Double::parseDouble).orElse(def);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String promptString(String msg, String def) {
        TextInputDialog dialog = new TextInputDialog(def);
        dialog.setTitle("Ввід");
        dialog.setHeaderText(null);
        dialog.setContentText(msg);
        return dialog.showAndWait().orElse(def);
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}