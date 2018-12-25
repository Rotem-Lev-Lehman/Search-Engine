package View;

import AnalizeTools.Analizer;
import Control.AController;
import Control.Controller;
import Model.*;
import Model.SecondPart.MyQuery;
import Model.SecondPart.SecondPartModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * The main class of the application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Analizer analizer = new Analizer();
        //analizer.AnalizeAmountOfNumbers("C:\\Users\\User\\Desktop\\אחזור מידע\\totalIndex\\totalIndices\\totalIndices\\numbers");

        SecondPartModel secondPartModel = new SecondPartModel();
        File folder = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\totalIndices\\totalIndices");
        File stopWords = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\stop_words.txt");
        File qrels = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\part2\\qrels.txt");
        secondPartModel.LoadDictionary(folder,false);
        secondPartModel.LoadStopwords(stopWords);
        secondPartModel.LoadQrels(qrels);
        MyQuery query = new MyQuery("Falkland petroleum exploration", "351");
        List<DocumentsDictionaryEntrance> data = secondPartModel.Search(query, false);
        for(int i = 0; i < data.size(); i++){
            System.out.println("i = " + i + ", doc = " + data.get(i).getDocNo());
        }
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
