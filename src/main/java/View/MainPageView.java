package View;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    /**
     * The open stop words button
     */
    public Button openStopWordsButton;

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
     * Start the DirectoryChooser which enables picking the wanted Directory
     */
    public void ChooseFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select the wanted File");

        Stage currentStage = (Stage) openStopWordsButton.getScene().getWindow();
        File selectedFile = chooser.showOpenDialog(currentStage);

        if(selectedFile != null && selectedFile.isFile()) {
            setChanged();
            notifyObservers(selectedFile);
        }
    }

    /**
     * Notify the user that the parsing operation was finished
     */
    public void NotifyDone(){
        labelDone.setVisible(true);
    }

    /**
     * Notify the user that the parsing operation was finished
     */
    public void NotifyStopWordsLoaded(){
        openStopWordsButton.setTextFill(Paint.valueOf("#11ff00"));
    }

    /** An Event handler for when the open stop words button is pressed
     * @param actionEvent - The event's argument
     */
    public void OpenStopWords(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("Open Stop words");
    }
}
