package View;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * The main page view controller
 */
public class MainPageView extends AView {

    /**
     * The open all files button
     */
    public Button openAllFilesButton;
    /**
     * The label that states we have finished parsing
     */
    public Label labelDone;

    /** An Event handler for when the open all files button is pressed
     * @param actionEvent - The event's argument
     */
    public void OpenAllFiles(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Open Files");
    }

    /**
     * Start the DirectoryChooser which enables picking the wanted Directory
     */
    public void ChooseDirectory(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the wanted Directory");

        Stage currentStage = (Stage) openAllFilesButton.getScene().getWindow();
        File selectedDirectory = chooser.showDialog(currentStage);

        if(selectedDirectory != null && selectedDirectory.isDirectory()) {
            setChanged();
            notifyObservers(selectedDirectory);
        }
    }

    /**
     * Notify the user that the parsing operation was finished
     */
    public void NotifyDone(){
        labelDone.setVisible(true);
    }
}
