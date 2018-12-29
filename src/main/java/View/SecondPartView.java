package View;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SecondPartView extends AView implements Initializable {

    public CheckBox useStem;

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

    public void RegularSearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[2];
        send[0] = "Regular search";
        send[1] = (Boolean)useStem.isSelected();

        setChanged();
        notifyObservers(send);
    }

    public void QueriesFileSearchPressed(ActionEvent actionEvent) {
        Object[] send = new Object[2];
        send[0] = "Queries file search";
        send[1] = (Boolean)useStem.isSelected();

        setChanged();
        notifyObservers(send);
    }

    public void LoadIndexPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Load index");
    }

    public void LoadStopWordsPressed(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Load stop words");
    }

    public File GetIndexDirectory(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose your total index folder");

        return chooser.showDialog(currStage);
    }

    public File GetStopWordsFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose the stop words file");

        return chooser.showOpenDialog(currStage);
    }
}
