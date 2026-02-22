import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Starter extends Application {
    static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/LoginView.fxml"));
        primaryStage.setTitle("Library System");

        // This removes the white top bar and OS buttons
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
