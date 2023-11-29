package com.example.demo;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CalorieCalculator extends Application {
    ObservableList<ResultEntry> list = FXCollections.observableArrayList();
    public class ResultEntry {
        private int weight;
        private int height;
        private int age;
        private String gender;
        private String activityLevel;
        private double calorieRate;

        public ResultEntry(int weight, int height, int age, String gender, String activityLevel, double calorieRate) {
            this.weight = weight;
            this.height = height;
            this.age = age;
            this.gender = gender;
            this.activityLevel = activityLevel;
            this.calorieRate = calorieRate;
        }

        public int getWeight() {
            return weight;
        }

        public int getHeight() {
            return height;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getActivityLevel() {
            return activityLevel;
        }

        public double getCalorieRate() {
            return calorieRate;
        }
    }

    private TableView<ResultEntry> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the root layout
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // Create input fields
        Label weightLabel = new Label("Weight (kg):");
        TextField weightTextField = new TextField();
        weightTextField.setPromptText("Enter weight");

        Label heightLabel = new Label("Height (cm):");
        TextField heightTextField = new TextField();
        heightTextField.setPromptText("Enter height");

        Label ageLabel = new Label("Age (years):");
        TextField ageTextField = new TextField();
        ageTextField.setPromptText("Enter age");

        Label genderLabel = new Label("Gender:");
        RadioButton maleRadioButton = new RadioButton("Male");
        RadioButton femaleRadioButton = new RadioButton("Female");
        ToggleGroup genderToggleGroup = new ToggleGroup();
        maleRadioButton.setToggleGroup(genderToggleGroup);
        femaleRadioButton.setToggleGroup(genderToggleGroup);

        Label activityLabel = new Label("Activity Level:");
        ObservableList<String> activityLevels = FXCollections.observableArrayList(
                "sedentary", "lightly active", "moderately active", "active", "very active"
        );
        ComboBox<String> activityComboBox = new ComboBox<>(activityLevels);
        activityComboBox.setValue("sedentary");

        Button calculateButton = new Button("Calculate");

        // Initialize the table with columns
        table = new TableView<ResultEntry>();
        table.setMinSize(400, 200); // Set the size of the table

        TableColumn<ResultEntry, Integer> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<ResultEntry, Integer> heightColumn = new TableColumn<>("Height");
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<ResultEntry, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<ResultEntry, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<ResultEntry, String> activityLevelColumn = new TableColumn<>("Activity Level");
        activityLevelColumn.setCellValueFactory(new PropertyValueFactory<>("activityLevel"));

        TableColumn<ResultEntry, Double> calorieRateColumn = new TableColumn<>("Calorie Rate");
        calorieRateColumn.setCellValueFactory(new PropertyValueFactory<>("calorieRate"));

        // Add the columns to the table
        table.getColumns().addAll(weightColumn, heightColumn, ageColumn, genderColumn, activityLevelColumn, calorieRateColumn);

        // Set up the layout
        root.add(weightLabel, 0, 0);
        root.add(weightTextField, 1, 0);
        root.add(heightLabel, 0, 1);
        root.add(heightTextField, 1, 1);
        root.add(ageLabel, 0, 2);
        root.add(ageTextField, 1, 2);
        root.add(genderLabel, 0, 3);
        root.add(maleRadioButton, 1, 3);
        root.add(femaleRadioButton, 2, 3);
        root.add(activityLabel, 0, 4);
        root.add(activityComboBox, 1, 4);
        root.add(calculateButton, 1, 5);
        root.add(table, 0, 6, 3, 1);

        // Set up the scene
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setTitle("Calorie Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set up the event handler for the Calculate button
        calculateButton.setOnAction(event -> {
            try {
                int weight = Integer.parseInt(weightTextField.getText());
                int height = Integer.parseInt(heightTextField.getText());
                int age = Integer.parseInt(ageTextField.getText());
                if (weight <= 0 || height <= 0 || height > 500 || age <= 0 || age > 200) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter valid values for weight, height, and age.\n" +
                            "Weight and the height must be non-negative and greater than 500, and age should be between 0 and 200.");
                    alert.showAndWait();
                    return;
                }
                boolean isMale = maleRadioButton.isSelected();
                String activityLevel = activityComboBox.getValue();
                String isMale1;
                if (isMale == true) {
                    isMale1 = "Male";
                } else isMale1 = "Female";
                double calorieRate = calculateCalorieRate(weight, height, age, isMale1, activityLevel);
                ResultEntry resultEntry = new ResultEntry(weight, height, age, isMale1, activityLevel, calorieRate);
                list.add(resultEntry);
                // Update the table with the new entry
                table.setItems(list);

            } catch (NumberFormatException e) {
                // Handle the exception (e.g., show an alert to the user)
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please enter valid numeric values for weight, height, and age.");
                alert.showAndWait();
            }
        });
        Button saveButton = new Button("Save");
        root.add(saveButton, 2, 5);

        // Set up the event handler for the Save button
        saveButton.setOnAction(event -> {
            try {
                saveTableDataToFile();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception (e.g., show an alert to the user)
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Save Error");
                alert.setHeaderText("Error saving data to file");
                alert.setContentText("An error occurred while saving data to the file.");
                alert.showAndWait();
            }
        });
    }
    private void saveTableDataToFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data to Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("текстовые файлы(*.txt)","*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (ResultEntry entry : list) {
                    writer.write(String.format("%d\t%d\t%d\t%s\t%s\t%.2f%n",
                            entry.getWeight(), entry.getHeight(), entry.getAge(),
                            entry.getGender(), entry.getActivityLevel(), entry.getCalorieRate()));
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File Saved");
                alert.setHeaderText("Data saved successfully");
                alert.setContentText("The data has been saved to the file.");
                alert.showAndWait();
            }
        }
    }
    private double calculateCalorieRate(int weight, int height, int age, String isMale, String activityLevel) {
        double baseCalories;

        if (isMale == "Male") {
            baseCalories = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            baseCalories = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        switch (activityLevel) {
            case "sedentary":
                baseCalories *= 1.2;
                break;
            case "lightly active":
                baseCalories *= 1.375;
                break;
            case "moderately active":
                baseCalories *= 1.55;
                break;
            case "active":
                baseCalories *= 1.725;
                break;
            case "very active":
                baseCalories *= 1.9;
                break;
            default:
                System.err.println("Invalid activity level");
        }

        return baseCalories;
    }
}
