package View;

import AnalizeTools.Analizer;
import Control.AController;
import Control.Controller;
import Model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Analizer analizer = new Analizer();
        //analizer.AnalizeAmountOfNumbers("C:\\Users\\User\\Desktop\\אחזור מידע\\totalIndex\\totalIndices\\totalIndices\\numbers");
        AModel model = new Model();
        AController controller = new Controller();
        controller.setModel(model);

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/MainPage.fxml").openStream());

        primaryStage.setTitle("Search Engine");
        primaryStage.setScene(new Scene(root, 773, 605));
        primaryStage.show();

        ((AView) fxmlLoader.getController()).setController(controller);
    }


    /** The main function of the application. runs the entire application
     * @param args - arguments for running the application with
     */
    public static void main(String[] args) {
        launch(args);
    }
}
