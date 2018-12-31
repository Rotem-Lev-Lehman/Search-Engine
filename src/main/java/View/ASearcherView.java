package View;

import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class ASearcherView extends AView {

    protected List<String> allCities;

    public List<String> getAllCities() {
        return allCities;
    }

    public void setAllCities(List<String> allCities) {
        this.allCities = allCities;
    }

    protected void moveSelectedToTheOtherList(ListView<String> from, ListView<String> to){
        if (from.getSelectionModel().getSelectedItem() != null) {
            String selected = from.getSelectionModel().getSelectedItem();
            to.getItems().add(selected);
            from.getItems().remove(selected);
            Collections.sort(to.getItems()); //added a new item so it needs to be sorted...
        }
    }

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
