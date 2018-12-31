package View;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;

public class SearchRegularQueryView extends ASearcherView {
    public CheckBox useSemantics;
    public CheckComboBox<String> checkComboBoxCitiesRelevant;
    public TextField textFieldQuery;

    @Override
    public void SearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[3];
        send[0] = textFieldQuery.getText();
        send[1] = new ArrayList<String>(checkComboBoxCitiesRelevant.getItems());
        send[2] = (Boolean)useSemantics.isSelected();

        //text,cities relevant,semantic search?
        setChanged();
        notifyObservers(send);
    }

}
