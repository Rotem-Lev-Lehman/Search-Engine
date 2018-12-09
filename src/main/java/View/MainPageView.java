package View;

import AnalizeTools.Analizer;
import AnalizeTools.TermFreqTuple;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    public TextField languageText;

    public TextField DSTPath;
    public TextField SRCPath;
    public Button srcPathOfStopWordsAndCorpus;
    public Button destinationPath;
    public CheckBox stemming;

    public Button resetBTN;
    public Button showBTN;
    public Button loadBTN;

    public List<String> languages;


    /**
     * Start the DirectoryChooser which enables picking the wanted Directory
     */
    public void ChooseDirectory(String pathType){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select the wanted Directory");
        if(pathType=="SRC") {
            Stage currentStage = (Stage) srcPathOfStopWordsAndCorpus.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            if(selectedDirectory == null)
                return;
            SRCPath.setText(selectedDirectory.getAbsolutePath());
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                setChanged();
                notifyObservers(selectedDirectory);
            }
        }
        else {
            Stage currentStage = (Stage) destinationPath.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            if(selectedDirectory == null)
                return;
            DSTPath.setText(selectedDirectory.getAbsolutePath());
            destinationPath.setTextFill(Paint.valueOf("#11ff00"));
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
        if ( !(DSTPath.getText().equals("") | SRCPath.getText().equals(""))) {
            languages = new ArrayList<>();
            languagesChoiceBox.getItems().removeAll(languagesChoiceBox.getItems());
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("language.txt").getFile());
                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                //System.out.println("hey");
                while (scanner.hasNext()) {
                    String thisLine = scanner.nextLine();
                    languages.add(thisLine.substring(1, thisLine.length() - 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //languages;
            languagesChoiceBox.getItems().addAll(languages);
            setChanged();
            notifyObservers("start");
        }
        else {
            JOptionPane.showMessageDialog(null, "Re-enter SRC and DST path's");
        }
    }
    public void deleteDirectory(File directoryToBDeleted){
        File[] Files = directoryToBDeleted.listFiles();
        if(Files != null) {
            for(File file : Files){
                deleteDirectory(file);
            }
        }
        directoryToBDeleted.delete();
    }

    public void resetProg(ActionEvent actionEvent) {
        File root = new File(DSTPath.getText());
        deleteDirectory(root);
        DSTPath.setText("");
        SRCPath.setText("");
        srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#333333ff"));
        destinationPath.setTextFill(Paint.valueOf("#333333ff"));
        languagesChoiceBox.getItems().removeAll(languagesChoiceBox.getItems());
        setChanged();
        notifyObservers("reset");
    }

    public void show(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("show");


    }

    public void load(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("load");
    }

    public void stem(ActionEvent actionEvent) {
        if (stemming.isSelected()==true) {
            setChanged();
            notifyObservers("stem");
        }
        else{
            setChanged();
            notifyObservers("not stem");
        }
    }
}
