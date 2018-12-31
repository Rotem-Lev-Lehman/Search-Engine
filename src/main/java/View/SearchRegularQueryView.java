package View;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class SearchRegularQueryView extends ASearcherView implements Initializable {
    public CheckBox useSemantics;
    public TextField textFieldQuery;
    public ListView<String> listFilterByCities;
    public ListView<String> listAvailableCities;

    @Override
    public void SearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[3];
        send[0] = textFieldQuery.getText();
        send[1] = new ArrayList<String>(listFilterByCities.getItems());
        send[2] = (Boolean)useSemantics.isSelected();

        //text,cities relevant,semantic search?
        setChanged();
        notifyObservers(send);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listAvailableCities.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                moveSelectedToTheOtherList(listAvailableCities, listFilterByCities);
            }
        });
        listFilterByCities.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                moveSelectedToTheOtherList(listFilterByCities, listAvailableCities);
            }
        });
        listFilterByCities.setPlaceholder(new Label("Empty"));
        listAvailableCities.setPlaceholder(new Label("Empty"));
    }

    @Override
    public void setAllCities(List<String> allCities) {
        super.setAllCities(allCities);

        listAvailableCities.getItems().addAll(this.allCities);
    }
}


