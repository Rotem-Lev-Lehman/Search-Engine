package View;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
     * The label that states we have finished parsing
     */
    public Label labelDone;
    public Button startButton;
    public ChoiceBox<String> languagesChoiceBox;


    public TextField DSTPath;
    public TextField SRCPath;
    public Button srcPathOfStopWordsAndCorpus;
    public Button destinationPath;

    /**
     * Start the DirectoryChooser which enables picking the wanted Directory
     */
    public void ChooseDirectory(String pathType){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the wanted Directory");
        if(pathType=="SRC") {
            Stage currentStage = (Stage) srcPathOfStopWordsAndCorpus.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            SRCPath.setText(selectedDirectory.getAbsolutePath());
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                setChanged();
                notifyObservers(selectedDirectory);
            }
        }
        else {
            Stage currentStage = (Stage) destinationPath.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            DSTPath.setText(selectedDirectory.getAbsolutePath());
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                setChanged();
                notifyObservers(selectedDirectory);
            }
        }
    }

    /**
     * Notify the user that the parsing operation was finished
     */
    public void NotifyDone(){
        srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#11ff00"));
        labelDone.setVisible(true);
    }

    public void openSrcPath(ActionEvent actionEvent){
        setChanged();
        notifyObservers("Given Source Files");
    }
    public void openDstPath(ActionEvent actionEvent){
        setChanged();
        notifyObservers("Destination Files");
    }


    /**
     * Notify the user that the parsing operation was finished
     */
    public void NotifySrcLoaded(){
        srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#11ff00"));
    }


    public void letsStart(ActionEvent actionEvent) {

//        public void initialize() {
//            languagesChoiceBox.getItems().removeAll(languagesChoiceBox.getItems());
//            languagesChoiceBox.getItems().addAll("English" , "Hebrew", "Spanish", "Japanese");
//            languagesChoiceBox.getSelectionModel().select("English");
//        }
        setChanged();
        notifyObservers("start");

    }
}
