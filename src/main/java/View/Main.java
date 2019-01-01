package View;

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
import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Analizer analizer = new Analizer();
        //analizer.AnalizeAmountOfNumbers("C:\\Users\\User\\Desktop\\אחזור מידע\\totalIndex\\totalIndices\\totalIndices\\numbers");

/*
        SecondPartModel secondPartModel = new SecondPartModel();
        File folder = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\totalIndices\\totalIndices");
        File stopWords = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\stop_words.txt");
        File qrels = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\part2\\qrels.txt");
        File queries = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\part2\\queries.txt");
        File results = new File("C:\\Users\\Rotem\\Desktop\\תואר ראשון\\אחזור\\part2\\results.txt");
        secondPartModel.LoadDictionary(folder,false);
        secondPartModel.LoadStopwords(stopWords);
        secondPartModel.LoadQrels(qrels);
        List<String> citiesRelevant = new ArrayList<>();

        List<MyQuery> myQueries = secondPartModel.Search(queries,citiesRelevant,true);
        secondPartModel.WriteResultsToFile(results, myQueries);
        */
        //citiesRelevant.add("TOKYO");
        /*
        MyQuery query = new MyQuery("Falkland petroleum exploration", citiesRelevant,"351");
        List<DocumentsDictionaryEntrance> data = secondPartModel.Search(query, true);
        for(int i = 0; i < data.size(); i++){
            System.out.println("i = " + i + ", doc = " + data.get(i).getDocNo());
        }
        */

        AController controller = new Controller();

        AModel model = new Model();
        controller.setModel(model);

        SecondPartModel secondPartModel = new SecondPartModel();
        controller.setSecondPartModel(secondPartModel);

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/MainPage.fxml").openStream());

        primaryStage.setTitle("Rotem & Ofir's Search Engine");
        primaryStage.setScene(new Scene(root, 630, 400));
        primaryStage.show();

        AView view = (AView) fxmlLoader.getController();
        view.setController(controller);
        view.setCurrStage(primaryStage);
        /*
        IdentityAndScore[] id = new IdentityAndScore[5];
        for(int i = 0; i < id.length; i++){
            id[i] = new IdentityAndScore("Hi", 0.5);
        }
        QueryResultForView queryResultForView = new QueryResultForView("123", new DocumentsDictionaryEntrance("docNo","file",2,4,"city",id));
        List<QueryResultForView> results = new ArrayList<QueryResultForView>();
        results.add(queryResultForView);
        ((ShowResultsView)fxmlLoader.getController()).setResults(results);
        */
    }


    /** The main function of the application. runs the entire application
     * @param args - arguments for running the application with
     */
    public static void main(String[] args) {
        launch(args);
    }
}
