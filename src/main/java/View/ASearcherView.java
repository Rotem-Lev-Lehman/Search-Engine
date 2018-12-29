package View;

import javafx.event.ActionEvent;

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

    public void MoveToShowResults(List<QueryResultForView> results){
        AView view = ChangeView("ShowResultsPage.fxml", "Results", 700, 500);
        ((ShowResultsView)view).setResults(results);
    }
}
