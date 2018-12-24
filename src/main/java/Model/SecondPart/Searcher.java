package Model.SecondPart;

import Model.*;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.File;
import java.util.*;

public class Searcher {
    private IParse parser;
    private HashSet<String> stopWords;
    private boolean toStem;
    private TotalDictionaryController totalDictionaryController;
    private SnowballStemmer stemmer;
    private boolean useSemantic;

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
                List<List<Term>> similarTerms = new ArrayList<>();
                for (Term term : parsedWithoutStemming) {
                    List<Term> currentSimilarTerms = SemanticSearcher(term);
                    similarTerms.add(currentSimilarTerms);
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
                }

                GeneratePermutations(similarTerms, subQueries, 0, new SubQuery(query.getId()), subQueryIndex);

                for (SubQuery subQuery : subQueries) {
                    querysTerms.addAll(subQuery.getQueryTerms());
                }
            } else {
                List<Term> terms = parser.Parse(query.getDocument(), stopWords, toStem);
                SubQuery curr = new SubQuery(query.getId());
                curr.setSubQueryNum(subQueryIndex.getValue());
                for (Term term : terms) {
                    curr.addTerm(term);
                }
                subQueries.add(curr);
                querysTerms.addAll(curr.getQueryTerms());
            }
        }

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

        //now search in every dictionary if the terms exist in it
        //merge all the data to a single list (so the read of the posting file will be with only one pass over it
        List<DocumentAndTermDataForRanking> totalData = new ArrayList<>();
        for(int i = 0; i < 26; i++) {
            totalData.addAll(searchInDictionary(totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.SmallLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.SmallLetters, i), smallLetterTerms.get(i)));
            totalData.addAll(searchInDictionary(totalDictionaryController.getDictionaryFromLetters(TypeOfTerm.BigLetters, i), totalDictionaryController.getPostingFromLetters(TypeOfTerm.BigLetters, i), bigLetterTerms.get(i)));
        }
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Number), totalDictionaryController.getPosting(TypeOfTerm.Number), numbersTerms));
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.RangeOrPhrase), totalDictionaryController.getPosting(TypeOfTerm.RangeOrPhrase), rangeOrPhraseTerms));
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.City), totalDictionaryController.getPosting(TypeOfTerm.City), cityTerms));
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Price), totalDictionaryController.getPosting(TypeOfTerm.Price), priceTerms));
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Percentage), totalDictionaryController.getPosting(TypeOfTerm.Percentage), percentageTerms));
        totalData.addAll(searchInDictionary(totalDictionaryController.getDictionary(TypeOfTerm.Date), totalDictionaryController.getPosting(TypeOfTerm.Date), dateTerms));
        //split the data to the given queries
        for(DocumentAndTermDataForRanking data : totalData){
            MyQuery query = findQuery(queries, data.getQueryID());
            SubQuery subQuery = query.getSubQueries().get(data.getNumOfSubQuery());

            subQuery.addToData(data);
        }
        //rank the queries
        for(MyQuery query : queries){
            Ranker ranker = new Ranker();
            ranker.Rank(query);
        }
        //done
    }

    private MyQuery findQuery(List<MyQuery> queries, String queryID){
        for(MyQuery query : queries) {
            if (query.getId().equals(queryID))
                return query;
        }
        return null;
    }

    private List<DocumentAndTermDataForRanking> searchInDictionary(Map<String, ADictionaryEntrance> dictionary, File postingFile, List<QuerysTerm> terms){
        //sort so will be searched in the index only with one pass on it
        terms.sort(new Comparator<QuerysTerm>() {
            @Override
            public int compare(QuerysTerm o1, QuerysTerm o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        List<DocumentAndTermDataForRanking> documentAndTermDataForRankings = new ArrayList<>();

        PostingFileReader postingFileReader = new PostingFileReader(postingFile);

        for(QuerysTerm term : terms) {
            ADictionaryEntrance dictionaryEntrance = dictionary.get(term.getValue());
            if (dictionaryEntrance == null) {
                //don't exist - ignore it...
                continue;
            }
            //read the wanted line
            postingFileReader.readLineNum(dictionaryEntrance.getPostingPtr());
            if(!postingFileReader.isDone()){
                ArrayList<EntranceRow> row = postingFileReader.getCurrent().getEntranceRows();
                for(EntranceRow entranceRow : row){
                    DocumentAndTermDataForRanking data = new DocumentAndTermDataForRanking(totalDictionaryController.getDocumentsDictionaryEntrance(entranceRow.getDocId()),dictionaryEntrance,entranceRow,term.getTerm().getPosition(), term.getQueryID(), term.getSubQueryNum());
                    documentAndTermDataForRankings.add(data);
                }
            }
        }

        postingFileReader.close();

        return documentAndTermDataForRankings;
    }
}
