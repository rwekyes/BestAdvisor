package edu.advising.ui;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.WindowEvent;
import java.text.BreakIterator;

public class BestAdvisorApp extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

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

        VBox layout = new VBox(15);
        layout.getChildren().addAll(
                navTitle,
                dashboardButton,
                studentButton,
                advisorButton
        );

        root.setLeft(navigationPanel);

        // Top Bar

        HBox topbar = new HBox();
        topbar.setPadding( new Insets(15));
        Label appTitle = new Label("Welcome to WebAdvisor!");

        topbar.getChildren().add(appTitle);

        root.setTop(topbar);

        // ContentArea ContentArea ContentArea
        StackPane ContentArea = new StackPane();
        Label ContentLabel = new Label("Content: WebAdvisor!"); //Changing the text to something else

        Button switchButton = new Button("Test");

        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(ContentLabel, switchButton);

        ContentArea.getChildren().addAll(centerLayout);

        root.setCenter(ContentArea);

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

        root.setBottom(statusBar);
        // Finishing stuff

        Scene mainScene = new Scene(root, 1200, 800);

        switchButton.setOnAction(e -> {
                    ContentLabel.setText("SceneManager swapped content!");
                }
        );

        window.setScene(mainScene);
        window.show();



    }
}
