package Control;

import AnalizeTools.Analizer;
import View.DictionaryViewer;
import View.MainPageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;

/**
 * A concrete class representing the controller of the application
 */
public class Controller extends AController {
    private String root;
    private String dest;
    private boolean stem = false;
    private Map<String,String> termFreqTuples;

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainPageView) {
            if (arg instanceof String) {
                if (arg.equals("Given Source Files"))
                    openDirectoryDialog((MainPageView) o, "SRC");
                else if(arg.equals("Destination Files")) {
                    openDirectoryDialog((MainPageView) o, "DST");
                }
                else if (arg.equals("start")) {
                    letsStart((MainPageView) o);
                }
                else if (arg.equals("reset")){
                    letsReset((MainPageView) o);
                }
                else if(arg.equals("show")){
                    letsShow((MainPageView) o);
                }
                else if(arg.equals("load")){
                    letsLoad((MainPageView) o);
                }
                else if(arg.equals("stem")){
                    stem=true;
                }
                else if(arg.equals("not stem")){
                    stem=false;
                }
            }
            else if(arg instanceof File){
                if(((File)arg).isDirectory()){
                    openAllFiles((MainPageView)o,(File)arg);}
            }
        }
    }
    private void letsLoad(MainPageView o) {
        //Save dict
        Analizer analizer = new Analizer();
        termFreqTuples = analizer.AnalizeForZipf(dest);
    }

    private void letsShow(MainPageView o) {
        //Show dict
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("/dictionaryViewer.fxml").openStream());
            ((DictionaryViewer)fxmlLoader.getController()).setTermFreqTuples(termFreqTuples);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage dictionary = new Stage();
        dictionary.setTitle("Dictionary viewer");
        dictionary.setScene(new Scene(root, 773, 605));
        dictionary.show();
    }

    private void letsReset(MainPageView o) {
        //RESET-done in the Main page view.
    }

    private void letsStart(MainPageView mainPageView){
        mainPageView.NotifyDone();
        model.SetDestinationPath(dest);
        if(stem==true){
            model.setStem(true);
        }
        else
            model.setStem(false);
        model.GetAllDocuments(root);
    }


    /** Open the files in the root Directory given
     * @param mainPageView - The View in with to say when the process has finished
     * @param file - The root Directory where all the file are in
     */
    private void openAllFiles(MainPageView mainPageView, File file) {

        mainPageView.NotifySrcLoaded();
        File[] matchingFiles = file.listFiles();
        if(matchingFiles.length==2) {
            if (matchingFiles[0].isFile()) {
                model.SetStopWords(matchingFiles[0]);
                root = matchingFiles[1].getAbsolutePath();
            } else if (matchingFiles[1].isFile()) {
                model.SetStopWords(matchingFiles[1]);
                root = matchingFiles[0].getAbsolutePath();
            }
        }
        else {
            if (matchingFiles.length == 0) {
                dest= file.getAbsolutePath();
            }
            else{
                System.out.println("Input error, Please read the README file again and start over the program.");
            }
        }

    }



    /** Open a directory dialog to choose the directory with the files to open
     * @param mainPageView - The View in with to open the directory dialog in
     */
    private void openDirectoryDialog(MainPageView mainPageView, String pathType) {
        mainPageView.ChooseDirectory(pathType);
    }
}