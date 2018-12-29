package View;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SecondPartView extends AView implements Initializable {

    public TextField textFieldQuery;
    public CheckComboBox<String> checkComboBoxCitiesRelevant;
    public CheckBox useSemantics;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ObservableList<String> strings = FXCollections.observableArrayList();

        /*
        ObservableList<String> strings = checkComboBoxCitiesRelevant.getItems();
        for (int i = 0; i <= 100; i++) {
            strings.add("Item " + i);
        }
        checkComboBoxCitiesRelevant.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                System.out.println(checkComboBoxCitiesRelevant.getCheckModel().getSelectedItems());
            }
        });
        */
    }

    public void SearchPressed(ActionEvent actionEvent) {
        String text = textFieldQuery.getText();
        boolean semanticSearch = useSemantics.isSelected();
        List<String> cities = new ArrayList<>(checkComboBoxCitiesRelevant.getItems());

        Object[] send;

        if (text.equals("")) {
            send = new Object[2];
            send[0] = "Search file";
            send[1] = cities;
        } else {
            send = new Object[2];
            send[0] = "Search";
            send[1] = text;
        }
        setChanged();
        notifyObservers(send);
    }

    public void SaveResultsPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Save results");
    }

    public void BrowsePressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Browse");
    }


    public void RegularSearchPressed(ActionEvent actionEvent) {
    }

    public void QueriesFileSearchPressed(ActionEvent actionEvent) {
    }
}
