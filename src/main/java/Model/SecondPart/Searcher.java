package Model.SecondPart;

import Model.*;
import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Searcher {
    private IParse parser;
    private HashSet<String> stopWords;
    private boolean toStem;
    private TotalDictionaryController totalDictionaryController;
    private SnowballStemmer stemmer;
    private boolean useSemantic;
    private int maxAmountOfSimilarWords = 5;

    public Searcher(HashSet<String> stopWords, boolean toStem, TotalDictionaryController totalDictionaryController, boolean useSemantic){
        this.stopWords = stopWords;
        parser = new Parse();
        this.toStem = toStem;
        this.useSemantic = useSemantic;
        this.totalDictionaryController = totalDictionaryController;
        this.stemmer = new porterStemmer();
    }

    private void GeneratePermutations(List<List<Term>> Lists, List<SubQuery> result, int depth, SubQuery current, MyInteger nextIndex)
    {
        if(depth == Lists.size())
        {
            current.setSubQueryNum(nextIndex.getValue());
            nextIndex.add(1);

            result.add(current);
            return;
        }

        for(int i = 0; i < Lists.get(depth).size(); i++)
        {
            SubQuery curr = new SubQuery(current);
            curr.addTerm(Lists.get(depth).get(i));

            GeneratePermutations(Lists, result, depth + 1, curr, nextIndex);
        }
    }

    public void SearchForRelevantDocuments(List<MyQuery> queries) {
        //create all of the queries data
        List<QuerysTerm> querysTerms = new ArrayList<>();
        for(MyQuery query : queries) {
            MyInteger subQueryIndex = new MyInteger(0);
            List<SubQuery> subQueries = new ArrayList<>();
            query.setSubQueries(subQueries);

            if (useSemantic) {
                List<Term> parsedWithoutStemming = parser.Parse(query.getDocument(), stopWords, false);
                SubQuery subQuery = new SubQuery(query.getId());
                //List<List<Term>> similarTerms = new ArrayList<>();
                for (Term term : parsedWithoutStemming) {
                    List<Term> currentSimilarTerms = SemanticSearcher(term);
                    if(toStem){
                        for(Term curr : currentSimilarTerms){
                            if(curr.getType() == TypeOfTerm.SmallLetters || curr.getType() == TypeOfTerm.BigLetters) {
                                stemmer.setCurrent(curr.getValue().toLowerCase());
                                if (stemmer.stem()) {
                                    if (curr.getType() == TypeOfTerm.BigLetters)
                                        curr.setValue(stemmer.getCurrent().toUpperCase());
                                    else
                                        curr.setValue(stemmer.getCurrent());
                                }
                            }
                        }
                    }
                    subQuery.addTerms(currentSimilarTerms);
                    //similarTerms.add(currentSimilarTerms);
                }

                //GeneratePermutations(similarTerms, subQueries, 0, new SubQuery(query.getId()), subQueryIndex);

                subQueries.add(subQuery);

                querysTerms.addAll(subQuery.getQueryTerms());
                /*
                for (SubQuery subQuery : subQueries) {
                    querysTerms.addAll(subQuery.getQueryTerms());
                }
                */
            } else {
                List<Term> terms = parser.Parse(query.getDocument(), stopWords, toStem);
                SubQuery curr = new SubQuery(query.getId());
                curr.setSubQueryNum(subQueryIndex.getValue());

                for (Term term : terms) {
                    curr.addTerm(term);
                    if(term.getType() == TypeOfTerm.SmallLetters){
                        Term big = new Term(term.getValue().toUpperCase(),term.getPosition(), TypeOfTerm.BigLetters);
                        curr.addTerm(big);
                    }
                    else if(term.getType() == TypeOfTerm.BigLetters){
                        Term small = new Term(term.getValue().toLowerCase(),term.getPosition(), TypeOfTerm.SmallLetters);
                        curr.addTerm(small);
                    }
                }

                subQueries.add(curr);
                querysTerms.addAll(curr.getQueryTerms());
            }
        }
        System.out.println("done generating queries terms");

        // split the terms to their different lists
        List<List<QuerysTerm>> smallLetterTerms = new ArrayList<>(26);
        List<List<QuerysTerm>> bigLetterTerms = new ArrayList<>(26);
        for(int i = 0; i < 26; i++)
        {
            smallLetterTerms.add(new ArrayList<QuerysTerm>());
            bigLetterTerms.add(new ArrayList<QuerysTerm>());
        }
        List<QuerysTerm> cityTerms = new ArrayList<QuerysTerm>();
        List<QuerysTerm> numbersTerms = new ArrayList<QuerysTerm>();
        List<QuerysTerm> rangeOrPhraseTerms = new ArrayList<QuerysTerm>();
        List<QuerysTerm> percentageTerms = new ArrayList<QuerysTerm>();
        List<QuerysTerm> priceTerms = new ArrayList<QuerysTerm>();
        List<QuerysTerm> dateTerms = new ArrayList<QuerysTerm>();
        for (QuerysTerm term : querysTerms) {
            if(term.getType() == TypeOfTerm.BigLetters) {
                char first = term.getValue().charAt(0);
                int index = first - 'A';
                bigLetterTerms.get(index).add(term);
            }
            else if(term.getType() == TypeOfTerm.City) {
                cityTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.SmallLetters) {
                char first = term.getValue().charAt(0);
                int index = first - 'a';
                smallLetterTerms.get(index).add(term);
            }
            else if(term.getType() == TypeOfTerm.Number) {
                numbersTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.RangeOrPhrase) {
                rangeOrPhraseTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.Percentage) {
                percentageTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.Price) {
                priceTerms.add(term);
            }
            else { // term.getType() == TypeOfTerm.Date
                dateTerms.add(term);
            }
        }

        System.out.println("starting to check posting");
        boolean checkCities = false;
        List<String> relevantCities = queries.get(0).getCitiesRelevant();
        if(relevantCities.size() > 0)
            checkCities = true;
        //now search in every dictionary if the terms exist in it
        //merge all the data to a single list (so the read of the posting file will be with only one pass over it
        ExecutorService threadPool = Executors.newFixedThreadPool(26*2 + 6);
        List<DocumentAndTermDataForRanking> totalData = new ArrayList<>();
        Object lock = new Object();
        for(int i = 0; i < 26; i++) {
            threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.SmallLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.SmallLetters, i), smallLetterTerms.get(i), checkCities, relevantCities));
            threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.BigLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.BigLetters, i), bigLetterTerms.get(i), checkCities, relevantCities));
        }

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.Number), totalDictionaryController.getPosting(TypeOfTerm.Number), numbersTerms, checkCities, relevantCities));

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.RangeOrPhrase), totalDictionaryController.getPosting(TypeOfTerm.RangeOrPhrase), rangeOrPhraseTerms, checkCities, relevantCities));

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.City), totalDictionaryController.getPosting(TypeOfTerm.City), cityTerms, checkCities, relevantCities));

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.Price), totalDictionaryController.getPosting(TypeOfTerm.Price), priceTerms, checkCities, relevantCities));

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.Percentage), totalDictionaryController.getPosting(TypeOfTerm.Percentage), percentageTerms, checkCities, relevantCities));

        threadPool.submit(new PostingSearcher(totalData, lock, totalDictionaryController.getDictionary(TypeOfTerm.Date), totalDictionaryController.getPosting(TypeOfTerm.Date), dateTerms, checkCities, relevantCities));

        threadPool.shutdown();
        try {
            boolean done = false;
            while (!done)
                done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("done reading from posting files");
        /*
        for(int i = 0; i < 26; i++) {
            totalData.addAll(searchInDictionary(totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.SmallLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.SmallLetters, i), smallLetterTerms.get(i)));
            totalData.addAll(searchInDictionary(totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.BigLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.BigLetters, i), bigLetterTerms.get(i)));
        }
        System.out.println("done letters");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Number), totalDictionaryController.getPosting(TypeOfTerm.Number), numbersTerms));
        System.out.println("done Numbers");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.RangeOrPhrase), totalDictionaryController.getPosting(TypeOfTerm.RangeOrPhrase), rangeOrPhraseTerms));
        System.out.println("done range-phrase");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.City), totalDictionaryController.getPosting(TypeOfTerm.City), cityTerms));
        System.out.println("done city");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Price), totalDictionaryController.getPosting(TypeOfTerm.Price), priceTerms));
        System.out.println("done price");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Percentage), totalDictionaryController.getPosting(TypeOfTerm.Percentage), percentageTerms));
        System.out.println("done percentage");
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Date), totalDictionaryController.getPosting(TypeOfTerm.Date), dateTerms));
        System.out.println("done date");
        */
        //split the data to the given queries
        for(DocumentAndTermDataForRanking data : totalData){
            MyQuery query = findQuery(queries, data.getQueryID());
            SubQuery subQuery = query.getSubQueries().get(data.getNumOfSubQuery());

            subQuery.addToData(data.getTermData().getTerm(), data);
        }
        System.out.println("done putting data to queries");
        //rank the queries
        for(MyQuery query : queries){
            StopWatch timer = new StopWatch();
            timer.start();
            Ranker ranker = new Ranker();
            ranker.Rank(query, totalDictionaryController.getAvgDocLength(), totalDictionaryController.getN());
            timer.stop();
            System.out.println("time for current query = " + timer.getTime(TimeUnit.MILLISECONDS) + " milliseconds");
        }
        //done
    }

    private List<Term> SemanticSearcher(Term term) {
        List<Term> terms = new ArrayList<Term>();
        terms.add(term);
        if(term.getType() == TypeOfTerm.SmallLetters || term.getType() == TypeOfTerm.BigLetters || term.getType() == TypeOfTerm.City) {
            String urlAddress = "https://api.datamuse.com/words?ml=" + term.getValue();
            try {
                URL url = new URL(urlAddress);
                URLConnection connection = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = reader.readLine();

                JSONArray array = new JSONArray(result);
                int length = Math.min(array.length(), maxAmountOfSimilarWords);

                for (int i = 0; i < length; i++) {
                    JSONObject current = (JSONObject) array.get(i);
                    String currTerm = (String) current.get("word");
                    String[] split = currTerm.split(" "); //check if is a phrase
                    if(split.length > 1){
                        //its a phrase...
                        Term t;
                        StringBuilder phrase = new StringBuilder();
                        for (int j = 0; j < split.length; j++) {
                            if(split[j].equals(""))
                                continue;
                            phrase.append(split[j]).append("-");
                        }
                        String p = phrase.toString();
                        p = p.substring(0, p.length() - 1); // remove the last "-"
                        t = new Term(p, term.getPosition(), TypeOfTerm.RangeOrPhrase);

                        t.setSemanticTerm(true);

                        terms.add(t);
                    }
                    else{
                        Term tSmall = new Term(currTerm, term.getPosition(), TypeOfTerm.SmallLetters);
                        Term tBig = new Term(currTerm.toUpperCase(), term.getPosition(), TypeOfTerm.BigLetters);

                        tSmall.setSemanticTerm(true);
                        tBig.setSemanticTerm(true);

                        terms.add(tSmall);
                        terms.add(tBig);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } //else - doesn't matter, because we only care in semantic about the regular terms...

        return terms;
    }

    private MyQuery findQuery(List<MyQuery> queries, String queryID){
        for(MyQuery query : queries) {
            if (query.getId().equals(queryID))
                return query;
        }
        return null;
    }

    private class PostingSearcher implements Runnable{

        private List<DocumentAndTermDataForRanking> totalData;
        private Object lock;
        private Map<String, ADictionaryEntrance> dictionary;
        private File postingFile;
        private List<QuerysTerm> terms;
        private boolean checkCities;
        private List<String> cities;

        public PostingSearcher(List<DocumentAndTermDataForRanking> totalData, Object lock, Map<String, ADictionaryEntrance> dictionary, File postingFile, List<QuerysTerm> terms, boolean checkCities, List<String> citiesRelevant) {
            this.totalData = totalData;
            this.lock = lock;
            this.dictionary = dictionary;
            this.postingFile = postingFile;
            this.terms = terms;
            this.checkCities = checkCities;
            this.cities = citiesRelevant;
        }

        @Override
        public void run() {
            if(terms.size() == 0)
                return;
            List<DocumentAndTermDataForRanking> postingResult = searchInDictionary(dictionary,postingFile,terms, checkCities, cities);
            synchronized (lock){
                totalData.addAll(postingResult);
            }
        }
    }

    private List<DocumentAndTermDataForRanking> searchInDictionary(Map<String, ADictionaryEntrance> dictionary, File postingFile, List<QuerysTerm> terms, boolean checkCities, List<String> citiesRelevant){
        //sort so will be searched in the index only with one pass on it
        terms.sort(new Comparator<QuerysTerm>() {
            @Override
            public int compare(QuerysTerm o1, QuerysTerm o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        HashMap<String, List<DocumentAndTermDataForRanking>> visited = new HashMap<>();

        List<DocumentAndTermDataForRanking> documentAndTermDataForRankings = new ArrayList<>();

        PostingFileReader postingFileReader = new PostingFileReader(postingFile);

        for(QuerysTerm term : terms) {
            List<DocumentAndTermDataForRanking> visitedList = visited.get(term.getValue());
            if(visitedList != null){
                documentAndTermDataForRankings.addAll(visitedList);
                continue;
            }

            ADictionaryEntrance dictionaryEntrance = dictionary.get(term.getValue());
            if (dictionaryEntrance == null) {
                //don't exist - ignore it...
                continue;
            }
            //read the wanted line
            postingFileReader.readLineNum(dictionaryEntrance.getPostingPtr());
            if(!postingFileReader.isDone()){
                ArrayList<EntranceRow> row = postingFileReader.getCurrent().getEntranceRows();
                visitedList = new ArrayList<>();
                for(EntranceRow entranceRow : row){
                    DocumentsDictionaryEntrance currDoc = totalDictionaryController.getDocumentsDictionaryEntrance(entranceRow.getDocId());

                    if(checkCities){
                        if(currDoc.getCity() == null)
                            continue;
                        if(!citiesRelevant.contains(currDoc.getCity()))
                            continue;
                    }

                    DocumentAndTermDataForRanking data = new DocumentAndTermDataForRanking(currDoc,dictionaryEntrance,entranceRow,term.getTerm().getPosition(), term.getQueryID(), term.getSubQueryNum());
                    //documentAndTermDataForRankings.add(data);
                    visitedList.add(data);
                }
                visited.put(term.getValue(), visitedList);
                documentAndTermDataForRankings.addAll(visitedList);
            }
        }

        postingFileReader.close();

        return documentAndTermDataForRankings;
    }
}
