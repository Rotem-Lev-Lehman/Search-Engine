package Control;

import AnalizeTools.Analizer;
import Model.Model;
import Model.SecondPart.MyQuery;
import Model.SecondPart.TotalDictionaryController;
import View.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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

    // second part stuff
    private TotalDictionaryController totalDictionaryController;
    private HashSet<String> stopWords;
    private boolean toStem;
    private List<MyQuery> queries;
    private boolean hasResults = false;

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
        else if(o instanceof ASearcherView) {
            //check generic stuff
            if (arg instanceof String) {
                if (arg.equals("Save results")) {
                    saveQueriesResults((ASearcherView) o);
                    return;
                } else if (arg.equals("Show results")) {
                    showQueriesResults((ASearcherView) o);
                    return;
                }
            }

            //check instances
            if(o instanceof SearchRegularQueryView) {
                if(arg instanceof Object[] && ((Object[])arg).length == 3 && ((Object[])arg)[0] instanceof String && ((Object[])arg)[1] instanceof List && ((Object[])arg)[2] instanceof Boolean){
                    searchRegularQuery((SearchRegularQueryView)o, (String)((Object[])arg)[0], (List<String>)((Object[])arg)[1], (Boolean)((Object[])arg)[2]);
                }
            }
            else if(o instanceof SearchQueryFileView){
                if(arg instanceof String && arg.equals("Browse")){
                    browseQueriesFile((SearchQueryFileView)o);
                }
                else if(arg instanceof Object[] && ((Object[])arg).length == 2 && ((Object[])arg)[0] instanceof List && ((Object[])arg)[1] instanceof Boolean){
                    SearchQueriesFile((SearchQueryFileView)o, (List<String>)((Object[])arg)[0], (Boolean)((Object[])arg)[1]);
                }
            }
        }
        else if(o instanceof SecondPartView){
            if(arg instanceof Object[] && ((Object[])arg).length == 2 && ((Object[])arg)[0] instanceof String && ((Object[])arg)[1] instanceof Boolean){
                if(((Object[])arg)[0].equals("Regular search")){
                    MoveToRegularSearch((SecondPartView)o, (Boolean)((Object[])arg)[1]);
                }
                else if(((Object[])arg)[0].equals("Queries file search")){
                    MoveToQueriesFileSearch((SecondPartView)o, (Boolean)((Object[])arg)[1]);
                }
            }
            else if(arg instanceof String){
                if(arg.equals("Load index")){
                    LoadIndex((SecondPartView)o);
                }
                else if(arg.equals("Load stop words")){
                    LoadStopWords((SecondPartView)o);
                }
            }
        }
    }

    private void LoadStopWords(SecondPartView secondPartView) {
        continue;
    }

    private void LoadIndex(SecondPartView secondPartView) {
        continue;
    }

    private void MoveToQueriesFileSearch(SecondPartView secondPartView, Boolean stem) {
        hasResults = false;
        continue;
    }

    private void MoveToRegularSearch(SecondPartView secondPartView, Boolean stem) {
        hasResults = false;
        continue;
    }

    private void SearchQueriesFile(SearchQueryFileView searchQueryFileView, List<String> citiesRelevant, Boolean useSemantics) {
        continue;
    }

    private void searchRegularQuery(SearchRegularQueryView searchRegularQueryView, String queryText, List<String> citiesRelevant, Boolean useSemantics) {
        continue;
    }

    private void searchQuery(SecondPartView secondPartView, String queryText) {
        MyQuery query = new MyQuery(queryText)
        secondPartModel.Search()
    }

    private void showQueriesResults(ASearcherView aSearcherView) {
        continue;
    }

    private void browseQueriesFile(SearchQueryFileView searchQueryFileView) {
        File queriesFile = searchQueryFileView.GetQueriesFile();
        if (queriesFile == null)
            searchQueryFileView.ShowFailure("You must peak a Queries file");

        List<MyQuery> browseQueries = secondPartModel.ReadQueriesFile(queriesFile);
        if (browseQueries == null) {
            searchQueryFileView.ShowFailure("You must peak a Queries file with the correct format, see the example Queries file from the Moodle to understand the correct format");
            return;
        }

        queries = browseQueries;
        hasResults = false;
        searchQueryFileView.ShowSuccess("We have successfully read the queries file, now ready to search!");
    }

    private void saveQueriesResults(ASearcherView aSearcherView) {
        if(hasResults){
            File destFolder = aSearcherView.GetResultsFolder();
            if(destFolder == null) {
                aSearcherView.ShowFailure("You have to choose a folder to save the results to");
                return;
            }

            File destFile = new File(destFolder.getAbsolutePath() + "//results.txt");
            boolean success = secondPartModel.WriteResultsToFile(destFile, queries);

            if(success)
                aSearcherView.ShowSuccess("The results file has been saved successfully");
            else
                aSearcherView.ShowFailure("There was an error while saving the results file");
        }
        else{
            aSearcherView.ShowFailure("You must search before you can save the results");
        }
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