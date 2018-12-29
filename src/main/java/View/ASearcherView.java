package View;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class ASearcherView extends AView {

    public abstract void SearchPressed(ActionEvent actionEvent);

    public void SaveResultsPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Save results");
    }

    public void ShowResultsPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Show results");
    }

    public File GetResultsFolder(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the directory where you would like the results file to be saved to");

        return chooser.showDialog(currStage);
    }

    public void MoveToShowResults(List<QueryResultForView> results){
        AView view = ChangeView("ShowResultsPage.fxml", "Results", 700, 500);
        ((ShowResultsView)view).setResults(results);
    }
}
