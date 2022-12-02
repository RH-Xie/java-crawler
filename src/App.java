import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage AppStage;
    private static FXMLLoader appLoader;

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            appLoader = new FXMLLoader(getClass().getResource("AppScene.fxml"));
            root = appLoader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("世界杯赛程信息");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.exit(0);
    }
    public static void main(String[] args) {
        launch();
    }
}
