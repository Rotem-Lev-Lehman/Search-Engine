package View;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main page view controller
 */
public class FirstPartView extends AView {
    /**
     * The label that states we have finished parsing
     */
    public Label labelDone;
    //public TextField languageText;

    public Button srcPathOfStopWordsAndCorpus;
    public TextField SRCPath;

    public Button destinationPath;
    public TextField DSTPath;

    public ChoiceBox<String> languagesChoiceBox;

    public CheckBox stemming;

    public Button startButton;

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
            //srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#11ff00"));
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                setChanged();
                notifyObservers(selectedDirectory);
            }
        }
        else if(pathType=="DST") {
            Stage currentStage = (Stage) destinationPath.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            if(selectedDirectory == null)
                return;
            DSTPath.setText(selectedDirectory.getAbsolutePath());
            //destinationPath.setTextFill(Paint.valueOf("#11ff00"));
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                setChanged();
                notifyObservers(selectedDirectory);
            }
        }
        else if(pathType=="LOAD") {
            Stage currentStage = (Stage) loadBTN.getScene().getWindow();
            File selectedDirectory = chooser.showDialog(currentStage);
            if (selectedDirectory == null) {
                ShowFailure("Empty Directory..");
                return;
            }
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
    public void NotifySrcLoaded(String str){
        if(str.equals("srcFiles")) {
            srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#11ff00"));
            ShowSuccess("Corpus successfully loaded");
        }
        else if (str.equals("dstFiles")){
            destinationPath.setTextFill(Paint.valueOf("#11ff00"));
            ShowSuccess("Destination file successfully loaded");
        }
        else if (str.equals("FinishedFirstPart")){
            ShowSuccess("Successfully built the index");
        }
        else if (str.equals("loadedDone")){
            ShowSuccess("Dictionary successfully loaded");
        }
        else if (str.equals("ShowError")){
            ShowFailure("You have to load the Dictionary first!");
        }
        else if (str.equals("todoReset")){
            ShowSuccess("Successfully reset the search engine");
        }
        else if (str.equals("Error")){
            //System.out.println("Input error, Please read the README file again and start over the program.");
            ShowFailure("Input error, Please read the README file again");
        }
    }


    /*

    public void deleteDirectory(File directoryToBDeleted){
        File[] Files = directoryToBDeleted.listFiles();
        if(Files != null) {
            for(File file : Files){
                deleteDirectoryInside(file);
            }
        }
    }
    */
    public void deleteDirectoryInside(File DirectoryInsideToBeDeleted){
        File[] Files = DirectoryInsideToBeDeleted.listFiles();
        if(Files != null) {
            for(File file : Files){
                deleteDirectoryInside(file);
            }
        }
        DirectoryInsideToBeDeleted.delete();
    }

    public void resetProg(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("reset");

    }

    public void activateReset(String toReset){
        File root = new File(toReset);
        deleteDirectoryInside(root);
        DSTPath.setText("");
        SRCPath.setText("");
        srcPathOfStopWordsAndCorpus.setTextFill(Paint.valueOf("#333333ff"));
        destinationPath.setTextFill(Paint.valueOf("#333333ff"));
        languagesChoiceBox.getItems().removeAll(languagesChoiceBox.getItems());
        stemming.setSelected(false);
    }

    public void show(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("show");
    }

    public void load(ActionEvent actionEvent) {
        setChanged();
        notifyObservers("load");
    }
    //--------------------------------------------------------------------------CHECKED
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
    //--------------------------------------------------------------------------
    public void letsStart(ActionEvent actionEvent) {
        if (!(DSTPath.getText().equals("") | SRCPath.getText().equals(""))) {
            languages = new ArrayList<>();
            languagesChoiceBox.getItems().removeAll(languagesChoiceBox.getItems());
            try {
                //String name = Main.class.getResource("/resources/language.txt").getFile();
                //ClassLoader classLoader = getClass().getClassLoader();
                //File file = new File(getClass().getResource("language.txt").toURI());
                //File file = new File(name);
                InputStream is = getClass().getResourceAsStream("language.txt");
                Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
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
        } else {
            ShowFailure("Couldn't start, No input..");
        }
    }

}
