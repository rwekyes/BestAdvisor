// Testing
// So far, most of the imports are not used, but they might be changed as the window...
// ...is being used.

package edu.advising.ui;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BestAdvisorApp extends Application {

    Button button;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Best Advisor");

        button = new Button();
        button.setText("Test");

        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        Scene scene = new Scene(layout, 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
