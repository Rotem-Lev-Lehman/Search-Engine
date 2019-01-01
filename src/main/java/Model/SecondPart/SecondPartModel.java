package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.util.*;

public class SecondPartModel {
    private TotalDictionaryController totalDictionaryController;
    private HashSet<String> stopWords;
    private boolean toStem;

    public void LoadDictionary(TotalDictionaryController totalDictionaryController, boolean isStemmed){
        this.totalDictionaryController = totalDictionaryController;
        this.toStem = isStemmed;
    }

    public void LoadStopWords(HashSet<String> stopWords){
        this.stopWords = stopWords;
    }

    public void Search(List<MyQuery> queries, boolean useSemantics){
        StopWatch stopWatch = new StopWatch();
        System.out.println("Starting to search in a bunch of Queries");
        stopWatch.start();

        Searcher searcher = new Searcher(stopWords,toStem,totalDictionaryController, useSemantics);
        searcher.SearchForRelevantDocuments(queries);

        stopWatch.stop();
        System.out.println("Finished searching in a bunch of Queries");
        long time = stopWatch.getTime();
        System.out.println("Total time = " + time + " milliseconds");
    }

    public List<MyQuery> ReadQueriesFile(File queriesFile) {
        try {
            List<MyQuery> queries = new ArrayList<>();
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(queriesFile)));
            String text = "";
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                text += line;
                text += "\n";
            }
            scanner.close();

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
                queries.add(new MyQuery(txt, new ArrayList<>(), queryID));
            }

            return queries;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean WriteResultsToFile(File dest, List<MyQuery> queries){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
            for(MyQuery query : queries){
                for(DocumentsDictionaryEntrance doc : query.getRetrievedDocuments()){
                    //                 query_id,    iter,       docno,       rank,     sim,    run_id
                    writer.write(query.getId() + " 0 " + doc.getDocNo().replace(" ", "") + " 1" + " 42.38" + " mt");
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

}
