package Control;

import AnalizeTools.Analizer;
import Model.Model;
import Model.TypeOfTerm;
import Model.SecondPart.MyQuery;
import Model.SecondPart.TotalDictionaryController;
import Model.DocumentsDictionaryEntrance;
import View.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

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
    private TotalDictionaryController totalDictionaryController = null;
    private HashSet<String> stopWords = null;
    private boolean toStem;
    private List<MyQuery> queries;
    private boolean hasResults = false;
    private List<String> allCities = null;

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
            }
        }
        else if(o instanceof MainPageView){
            if(arg instanceof String){
                if(arg.equals("Create Index")){
                    MoveToFirstPartPage((MainPageView)o);
                }
                else if(arg.equals("Search Queries")){
                    MoveToSecondPartPage((MainPageView)o);
                }
            }
        }
    }

    private void MoveToSecondPartPage(MainPageView mainPageView) {
        mainPageView.ChangeView("SecondPartPage.fxml", "Search Queries Page", 640, 400);
    }

    private void MoveToFirstPartPage(MainPageView mainPageView) {
        mainPageView.ChangeView("FirstPartPage.fxml", "Create Index Page", 773, 605);
    }

    private void LoadIndex(SecondPartView secondPartView) {
        File indexFolder = secondPartView.GetIndexDirectory();
        if (indexFolder == null) {
            secondPartView.ShowFailure("You must pick an Index folder");
            return;
        }
        try {
            TotalDictionaryController temp = new TotalDictionaryController(indexFolder);

            this.totalDictionaryController = temp;
            allCities = new ArrayList<>(totalDictionaryController.getDictionary(TypeOfTerm.City).keySet());
            Collections.sort(allCities);
            secondPartView.ShowSuccess("Loaded the index successfully!");

        } catch (Exception e) {
            secondPartView.ShowFailure("There was an error while trying to load the index, please make sure that you have chosen a folder with the correct format");
        }
    }

    private void MoveToQueriesFileSearch(SecondPartView secondPartView, Boolean stem) {
        if (checkMovingToSearchPage(secondPartView, stem)) {
            AView queriesFile = secondPartView.ChangeView("SearchQueryFilePage.fxml", "Search Queries file Page", 640, 420);
            ((ASearcherView)queriesFile).setAllCities(allCities);
        }
    }

    private void MoveToRegularSearch(SecondPartView secondPartView, Boolean stem) {
        if (checkMovingToSearchPage(secondPartView, stem)) {
            AView regular = secondPartView.ChangeView("SearchRegularQueryPage.fxml", "Search Regular Query Page", 620, 420);
            ((ASearcherView)regular).setAllCities(allCities);
        }
    }

    private boolean checkMovingToSearchPage(SecondPartView secondPartView, Boolean stem){
        if(totalDictionaryController == null){
            secondPartView.ShowFailure("You need to load the Index before you move to the search page");
            return false;
        }

        //everything is ready for moving
        hasResults = false;
        queries = null;
        toStem = stem;

        secondPartModel.LoadDictionary(totalDictionaryController, toStem);
        secondPartModel.LoadStopWords(new HashSet<>());

        return true;
    }

    private void SearchQueriesFile(SearchQueryFileView searchQueryFileView, List<String> citiesRelevant, Boolean useSemantics) {
        if(queries == null){
            searchQueryFileView.ShowFailure("Please browse a Queries file before searching");
            return;
        }

        //update the relevant cities for each query
        for(MyQuery query : queries){
            query.setCitiesRelevant(citiesRelevant);
        }

        secondPartModel.Search(queries, useSemantics);
        hasResults = true;

        searchQueryFileView.ShowSuccess("Done searching, now you can view the results and save them if you wish so");
    }

    private void searchRegularQuery(SearchRegularQueryView searchRegularQueryView, String queryText, List<String> citiesRelevant, Boolean useSemantics) {
        if(queryText == null || queryText.equals("")) {
            searchRegularQueryView.ShowFailure("Please enter a Query before searching");
            return;
        }

        MyQuery query = new MyQuery(queryText, citiesRelevant, "007"); //Lets see if you get the reference ;)

        queries = new ArrayList<MyQuery>(1);
        queries.add(query);

        secondPartModel.Search(queries, useSemantics);
        hasResults = true;

        searchRegularQueryView.ShowSuccess("Done searching, now you can view the results and save them if you wish so");
    }

    private void showQueriesResults(ASearcherView aSearcherView) {
        if(hasResults){
            List<QueryResultForView> resultsList = getResultsForViewList();
            aSearcherView.MoveToShowResults(resultsList);
        }
        else{
            aSearcherView.ShowFailure("You must search before you can view the results");
        }
    }

    private List<QueryResultForView> getResultsForViewList() {
        List<QueryResultForView> resultsList = new ArrayList<>();
        for (MyQuery query : queries) {
            int rank = 1;
            for (DocumentsDictionaryEntrance entrance : query.getRetrievedDocuments()) {
                resultsList.add(new QueryResultForView(query.getId(), rank, entrance));
                rank++;
            }
        }
        return resultsList;
    }

    private void browseQueriesFile(SearchQueryFileView searchQueryFileView) {
        File queriesFile = searchQueryFileView.GetQueriesFile();
        if (queriesFile == null) {
            searchQueryFileView.ShowFailure("You must pick a Queries file");
            return;
        }

        List<MyQuery> browseQueries = secondPartModel.ReadQueriesFile(queriesFile);
        if (browseQueries == null) {
            searchQueryFileView.ShowFailure("You must pick a Queries file with the correct format, see the example Queries file from the Moodle to understand the correct format");
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
        if(dest == null) {
            firstPartView.ShowFailure("Must load or create an index to reset one");
            return;
        }

        firstPartView.activateReset(dest);
        model=new Model();
        stem = false;
        src="";
        dest="";
        loadingSRC="";
        termFreqTuples= null;
        tmp="";
        System.gc();
        firstPartView.NotifySrcLoaded("todoReset");

    }



    /** Open the files in the root Directory given
     * @param firstPartView - The View in with to say when the process has finished
     * @param file - The root Directory where all the file are in
     */
    private void openAllFiles(FirstPartView firstPartView, File file) {

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
        firstPartView.NotifySrcLoaded("FinishedFirstPart");

    }
    /** Open a directory dialog to choose the directory with the files to open
     * @param firstPartView - The View in with to open the directory dialog in
     */
    private void openDirectoryDialog(FirstPartView firstPartView, String pathType) {
        firstPartView.ChooseDirectory(pathType);
    }


    private void letsLoad(FirstPartView o) {
        //Save dict
        Analizer analizer = new Analizer();
        try {
            termFreqTuples = analizer.AnalizeForZipf(loadingSRC);

            dest = loadingSRC;
            o.NotifySrcLoaded("loadedDone");
        }
        catch (Exception e){
            o.ShowFailure("You need to choose an Index file that is in the correct format that we have made. try to index again and then choose the 'totalIndices' folder");
        }

    }

    private void letsShow(FirstPartView o) {
        //Show dict
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("/dictionaryViewer.fxml").openStream());
            if (termFreqTuples != null) {
                ((DictionaryViewer) fxmlLoader.getController()).setTermFreqTuples(termFreqTuples);
            } else {
                o.NotifySrcLoaded("ShowError");
                return;
            }

            Stage dictionary = new Stage();
            dictionary.setTitle("Dictionary viewer");
            dictionary.setScene(new Scene(root, 773, 605));
            dictionary.initModality(Modality.APPLICATION_MODAL);
            dictionary.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}