package View;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

public class SearchQueryFileView extends ASearcherView {
    public TextField textFieldQuery;
    public CheckComboBox checkComboBoxCitiesRelevant;
    public CheckBox useSemantics;


    public void BrowsePressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Browse");
    }

    @Override
    public void SearchPressed(ActionEvent actionEvent) {

    }
}
