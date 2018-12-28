package Control;

import AnalizeTools.Analizer;
import Model.Model;
import View.DictionaryViewer;
import View.FirstPartView;
import View.SecondPartView;
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
    private String src;
    private String dest;
    private String loadingSRC;
    private boolean stem = false;
    private Map<String,String> termFreqTuples;
    private String tmp;

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FirstPartView) {
            if (arg instanceof String) {
                if (arg.equals("Given Source Files")){
                    tmp="SRC";
                    openDirectoryDialog((FirstPartView) o, "SRC");
                }
                else if(arg.equals("Destination Files")) {
                    tmp="DST";
                    openDirectoryDialog((FirstPartView) o, "DST");
                }
                else if(arg.equals("load")){
                    tmp="LOAD";
                    openDirectoryDialog((FirstPartView) o, "LOAD");
                }
                else if (arg.equals("start")) {
                    letsStart((FirstPartView) o);
                }
                else if (arg.equals("reset")){
                    letsReset((FirstPartView) o);
                }
                else if(arg.equals("show")){
                    letsShow((FirstPartView) o);
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
                    openAllFiles((FirstPartView)o,(File)arg);}
            }
        }
        else if(o instanceof SecondPartView){
            if(arg instanceof String){
                if(arg.equals("Search file")){
                    searchQueriesFile((SecondPartView)o);
                }
                else if(arg.equals("Save results")){
                    saveQueriesResults((SecondPartView)o);
                }
                else if(arg.equals("Browse")){
                    browseQueriesFile((SecondPartView)o);
                }
                else if(arg.equals("Show results")){
                    showQueriesResults((SecondPartView)o);
                }
            }
            else if(arg instanceof String[] && ((String[])arg).length == 2 && ((String[])arg)[0].equals("Search")){
                searchQuery((SecondPartView)o, ((String[])arg)[1]);
            }
        }
    }

    private void searchQuery(SecondPartView secondPartView, String queryText) {
        continue;
    }

    private void showQueriesResults(SecondPartView secondPartView) {
        continue;
    }

    private void browseQueriesFile(SecondPartView secondPartView) {
        continue;
    }

    private void saveQueriesResults(SecondPartView secondPartView) {
        continue;
    }

    private void searchQueriesFile(SecondPartView secondPartView) {
        continue;
    }


    private void letsReset(FirstPartView firstPartView) {
        System.gc();
        model=new Model();
        stem = false;
        src="";
        dest="";
        loadingSRC="";
        termFreqTuples= null;
        tmp="";
    }



    /** Open the files in the root Directory given
     * @param firstPartView - The View in with to say when the process has finished
     * @param file - The root Directory where all the file are in
     */
    private void openAllFiles(FirstPartView firstPartView, File file) {
//        System.out.println("Hey");

        File[] matchingFiles = file.listFiles();
        if (tmp.equals("SRC")) {
            if (matchingFiles.length == 2) {
                if (matchingFiles[0].isFile()) {
                    model.SetStopWords(matchingFiles[0]);
                    src = matchingFiles[1].getAbsolutePath();
                } else if (matchingFiles[1].isFile()) {
                    model.SetStopWords(matchingFiles[1]);
                    src = matchingFiles[0].getAbsolutePath();
                }
                firstPartView.NotifySrcLoaded("srcFiles");
            }
            else
            {
                firstPartView.NotifySrcLoaded("Error");
            }
        } else if (tmp.equals("DST")) {
            if (matchingFiles.length == 0) {
                dest = file.getAbsolutePath();
                firstPartView.NotifySrcLoaded("dstFiles");

            }
            else{
                firstPartView.NotifySrcLoaded("Error");
            }
        } else if (tmp.equals("LOAD")){
            loadingSRC = file.getAbsolutePath();
            letsLoad(firstPartView);
        } else{
            firstPartView.NotifySrcLoaded("Error");
        }
    }



    // DONE STUFF
    private void letsStart(FirstPartView firstPartView) {
        firstPartView.NotifyDone();
        model.SetDestinationPath(dest);
        if (stem) {
            model.setStem(true);
        } else
            model.setStem(false);
        model.GetAllDocuments(src);
    }
    /** Open a directory dialog to choose the directory with the files to open
     * @param firstPartView - The View in with to open the directory dialog in
     */
    private void openDirectoryDialog(FirstPartView firstPartView, String pathType) {
        firstPartView.ChooseDirectory(pathType);
    }


    //***************************************************************
    //FOR ROTEM TO FIX (SHOW DICTIONARY)
    private void letsLoad(FirstPartView o) {
        //Save dict
        Analizer analizer = new Analizer();
        termFreqTuples = analizer.AnalizeForZipf(loadingSRC);
    }

    private void letsShow(FirstPartView o) {
        //Show dict
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("/dictionaryViewer.fxml").openStream());
            if (termFreqTuples!=null) {
                ((DictionaryViewer) fxmlLoader.getController()).setTermFreqTuples(termFreqTuples);
            }
            else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage dictionary = new Stage();
        dictionary.setTitle("Dictionary viewer");
        dictionary.setScene(new Scene(root, 773, 605));
        dictionary.show();
    }
}