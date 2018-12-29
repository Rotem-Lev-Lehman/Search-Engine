package View;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.util.ArrayList;

public class SearchQueryFileView extends ASearcherView {
    public CheckComboBox<String> checkComboBoxCitiesRelevant;
    public CheckBox useSemantics;

    public void BrowsePressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Browse");
    }

    @Override
    public void SearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[2];
        send[0] = new ArrayList<String>(checkComboBoxCitiesRelevant.getItems());
        send[1] = (Boolean)useSemantics.isSelected();

        setChanged();
        notifyObservers(send);
    }

    public File GetQueriesFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose your Queries file");

        return chooser.showOpenDialog(currStage);
    }
}
