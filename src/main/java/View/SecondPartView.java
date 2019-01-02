package View;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class SecondPartView extends AView {

    public CheckBox useStem;

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
