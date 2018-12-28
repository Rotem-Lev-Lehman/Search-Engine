package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class SecondPartModel {
    private TotalDictionaryController totalDictionaryController;
    private HashSet<String> stopWords;
    private boolean toStem;
    HashMap<String, List<String>> qrels;

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
        StopWatch stopWatch = new StopWatch();
        System.out.println("Starting to search in a bunch of Queries");
        stopWatch.start();

        Searcher searcher = new Searcher(stopWords,toStem,totalDictionaryController, useSemantics);
        searcher.SearchForRelevantDocuments(queries);

        stopWatch.stop();
        System.out.println("Finished searching in a bunch of Queries");
        long time = stopWatch.getTime();
        System.out.println("Total time = " + time + " milliseconds");


        List<List<DocumentsDictionaryEntrance>> data = new ArrayList<>();
        for(MyQuery query : queries){
            data.add(query.getRetrievedDocuments());
        }

        CountRelevantDocuments(queries);

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

        CountRelevantDocuments(query.getRetrievedDocuments(),query.getId());
        return query.getRetrievedDocuments();
    }

    public void CountRelevantDocuments(List<DocumentsDictionaryEntrance> result, String qID){
        int count = 0;
        List<String> relevant = qrels.get(qID);
        for(DocumentsDictionaryEntrance entrance : result){
            String docid = entrance.getDocNo().replace(" ", "");
            if(relevant.contains(docid))
                count++;
        }
        System.out.println("relevant doc number = " + count);
        System.out.println("total amount of relevant docs = " + relevant.size());
    }

    public void CountRelevantDocuments(List<MyQuery> queries){
        int count = 0;
        int relevantCount = 0;
        for(MyQuery query : queries) {
            int currCount = 0;
            int currRelevantCount = 0;
            List<DocumentsDictionaryEntrance> documents = query.getRetrievedDocuments();
            List<String> relevant = qrels.get(query.getId());
            relevantCount += relevant.size();
            currRelevantCount = relevant.size();
            for (DocumentsDictionaryEntrance entrance : documents) {
                String docid = entrance.getDocNo().replace(" ", "");
                if (relevant.contains(docid)) {
                    count++;
                    currCount++;
                }
            }

            System.out.println("relevant doc number for curr query = " + currCount);
            System.out.println("amount of relevant docs for curr query = " + currRelevantCount);
        }
        System.out.println();
        System.out.println();
        System.out.println("total relevant doc number = " + count);
        System.out.println("total amount of relevant docs = " + relevantCount);
    }

    public void LoadQrels(File qrels){
        try {
            this.qrels = new HashMap<>();
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(qrels)));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] split = line.split(" ");
                String id = split[0];
                String doc = split[2];
                int rel = Integer.parseInt(split[3]);
                if(rel == 1)
                {
                    if(this.qrels.containsKey(id)){
                        this.qrels.get(id).add(doc);
                    }
                    else{
                        this.qrels.put(id,new ArrayList<>());
                        this.qrels.get(id).add(doc);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<List<DocumentsDictionaryEntrance>> Search(File queriesFile, List<String> citiesRelevant, boolean useSemantics){
        List<MyQuery> queries = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(queriesFile)));
            String text = "";
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                text += line;
                text += "\n";
            }
            String[] differentQueriesTexts = text.split("<top>");
            for (String queryText : differentQueriesTexts){
                if(queryText.equals(""))
                    continue;
                String[] numSplit = queryText.split("<num>");
                String[] titleSplit = numSplit[1].split("<title>");
                String[] number = numSplit[1].split("\n")[0].split(" ");
                int count = 0;
                String queryID = "";
                for(String str : number){
                    if(str.equals(""))
                        continue;
                    if(count == 1) {
                        queryID = str;
                        break;
                    }
                    count++;
                }
                String[] title = titleSplit[1].split("\n")[0].split(" ");
                StringBuilder queryBuilder = new StringBuilder();
                for(int i = 0; i < title.length; i++){
                    if(title[i].equals(""))
                        continue;
                    queryBuilder.append(title[i]);
                    if(i < title.length - 1)
                        queryBuilder.append(" ");
                }
                String txt = queryBuilder.toString();
                queries.add(new MyQuery(txt, citiesRelevant, queryID));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Search(queries, useSemantics);
    }

}
