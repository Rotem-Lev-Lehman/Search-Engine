package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class SecondPartModel {
    private TotalDictionaryController totalDictionaryController;
    private HashSet<String> stopWords;
    private boolean toStem;

    public void LoadDictionary(File totalIndicesFolder, boolean isStemmed){
        totalDictionaryController = new TotalDictionaryController(totalIndicesFolder);
        this.toStem = isStemmed;
    }

    public void LoadStopwords(File stopWordsFile){
        try {
            stopWords = new HashSet<String>();
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(stopWordsFile)));
            while (scanner.hasNext())
                stopWords.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<List<DocumentsDictionaryEntrance>> Search(List<MyQuery> queries, boolean useSemantics){
        Searcher searcher = new Searcher(stopWords,toStem,totalDictionaryController, useSemantics);
        searcher.SearchForRelevantDocuments(queries);

        List<List<DocumentsDictionaryEntrance>> data = new ArrayList<>();
        for(MyQuery query : queries){
            data.add(query.getRetrievedDocuments());
        }
        return data;
    }

    public List<DocumentsDictionaryEntrance> Search(MyQuery query, boolean useSemantics){
        StopWatch stopWatch = new StopWatch();
        System.out.println("Starting to search");
        stopWatch.start();

        Searcher searcher = new Searcher(stopWords,toStem,totalDictionaryController, useSemantics);
        List<MyQuery> queries = new ArrayList<>(1);
        queries.add(query);
        searcher.SearchForRelevantDocuments(queries);

        stopWatch.stop();
        System.out.println("Finished searching");
        long time = stopWatch.getTime();
        System.out.println("Total time = " + time + " milliseconds");

        return query.getRetrievedDocuments();
    }

    /*
    public List<List<DocumentsDictionaryEntrance>> Search(File queriesFile, boolean useSemantics){
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(queriesFile)));
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()){
                builder.append(scanner.next());
            }
            String text = builder.toString();
            String[] differentQueriesTexts = text.split("<top>");
            List<MyQuery> queries = new ArrayList<>(differentQueriesTexts.length);
            for (int i = 0; i < differentQueriesTexts.length; i++){
                continue here;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    */
}
