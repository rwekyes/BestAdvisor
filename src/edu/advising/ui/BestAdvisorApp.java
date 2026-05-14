package edu.advising.ui;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;

public class BestAdvisorApp extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;

        window.setTitle("Best Advisor");
        window.initStyle(StageStyle.DECORATED);

        window.setMinHeight(800);
        window.setMinWidth(1200);

        //Might change to true if resize acceptable.
        window.setResizable(false);

        BorderPane root = new BorderPane();

        // Navigation *Testing for Section 3


        VBox navigationPanel = new VBox(15);
        navigationPanel.setPrefWidth(220);
        navigationPanel.setPadding(new Insets(15));

        Label navTitle = new Label("Navigation Panel");

        Button dashboardButton = new Button("Dashboard");
        Button studentButton = new Button("Student");
        Button advisorButton = new Button("Advisor");

        navigationPanel.getChildren().addAll(
                navTitle,
                dashboardButton,
                studentButton,
                advisorButton
        );

        root.setLeft(navigationPanel);

        // Top Bar

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/views/TopBar.fxml"));

        HBox topBar = loader.load();

        // ContentArea ContentArea ContentArea
        StackPane contentArea = new StackPane();
        Label contentLabel = new Label("Content: WebAdvisor!"); //Changing the text to something else

        Button switchButton = new Button("Test");

        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(contentLabel, switchButton);

        contentArea.getChildren().addAll(centerLayout);

        root.setCenter(contentArea);

        // StatusBar StatusBar StatusBar StatusBar
        HBox statusBar = new HBox(25);
        statusBar.setPadding(new Insets(15));

        Label currentUser = new Label("User: Admin");
        Label sessionTime = new Label("Session: 00:00");
        Label messages = new Label("Messages: Ready");

        statusBar.getChildren().addAll(
                currentUser,
                sessionTime,
                messages
        );
        root.setTop(topBar);
        root.setBottom(statusBar);

        // Navigation Panel

        FXMLLoader navLoader =
                new FXMLLoader(
                        getClass().getResource("/views/NavigationPanel.fxml")
                );

        navigationPanel = navLoader.load();

        root.setLeft(navigationPanel);

        // Finishing stuff

        Scene mainScene = new Scene(root, 1200, 800);

        switchButton.setOnAction(e -> {
                    contentLabel.setText("SceneManager swapped content!");
                }
        );

        window.setScene(mainScene);
        window.show();

    }
}
