package View;

import Control.AController;
import Control.Controller;
import Model.AModel;
import Model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AModel model = new Model();
        AController controller = new Controller();
        controller.setModel(model);

        FXMLLoader fxmlLoader = new FXMLLoader();

        Parent root = fxmlLoader.load(getClass().getResourceAsStream("MainPage.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        ((AView) fxmlLoader.getController()).setController(controller);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
