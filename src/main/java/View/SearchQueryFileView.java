package View;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchQueryFileView extends ASearcherView implements Initializable {
    public CheckBox useSemantics;
    public ListView<String> listFilterByCities;
    public ListView<String> listAvailableCities;

    public void BrowsePressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Browse");
    }

    @Override
    public void SearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[2];
        send[0] = new ArrayList<String>(listFilterByCities.getItems());
        send[1] = (Boolean)useSemantics.isSelected();

        setChanged();
        notifyObservers(send);
    }

    public File GetQueriesFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose your Queries file");

        return chooser.showOpenDialog(currStage);
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
