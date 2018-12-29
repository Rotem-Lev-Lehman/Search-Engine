package View;

import javafx.event.ActionEvent;

public class MainPageView extends AView {
    public void CreateIndexPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Create Index");
    }

    public void SearchQueriesPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Search Queries");
    }
}
