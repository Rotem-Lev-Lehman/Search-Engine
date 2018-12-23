package Model.SecondPart;

import Model.*;

import java.io.File;
import java.util.*;

public class Searcher {
    private IParse parser;
    private HashSet<String> stopWords;
    private boolean toStem;
    private File indicesFolder;
    private DocumentsDictionaryController documentsDictionaryController;

    public Searcher(HashSet<String> stopWords, boolean toStem, File indicesFolder){
        this.stopWords = stopWords;
        parser = new Parse();
        this.toStem = toStem;
        this.indicesFolder = indicesFolder;
    }

    public void LoadDictionary(){
        continue
    }

    public List<DocumentsDictionaryEntrance> SearchRelevantDocuments(MyQuery query) {
        List<Term> terms = parser.Parse(query.getDocument(), stopWords, toStem);

        List<DocumentAndTermDataForRanking> candidates = new ArrayList<>();

        // split the terms to their different lists
        List<List<Term>> smallLetterTerms = new ArrayList<>(26);
        List<List<Term>> bigLetterTerms = new ArrayList<>(26);
        for(int i = 0; i < 26; i++)
        {
            smallLetterTerms.add(new ArrayList<Term>());
            bigLetterTerms.add(new ArrayList<Term>());
        }
        List<Term> cityTerms = new ArrayList<Term>();
        List<Term> numbersTerms = new ArrayList<Term>();
        List<Term> rangeOrPhraseTerms = new ArrayList<Term>();
        List<Term> percentageTerms = new ArrayList<Term>();
        List<Term> priceTerms = new ArrayList<Term>();
        List<Term> dateTerms = new ArrayList<Term>();
        for (Term term : terms) {
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
    }

    private List<DocumentAndTermDataForRanking> searchInDictionary(Map<String, ADictionaryEntrance> dictionary, File postingFile, List<Term> terms){
        //sort so will be searched in the index only with one pass on it
        terms.sort(new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        List<DocumentAndTermDataForRanking> documentAndTermDataForRankings = new ArrayList<>();

        PostingFileReader postingFileReader = new PostingFileReader(postingFile);

        for(Term term : terms) {
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
                    DocumentAndTermDataForRanking data = new DocumentAndTermDataForRanking(documentsDictionaryController.getDictionaryEntrance(entranceRow.getDocId()),dictionaryEntrance,entranceRow,term.getPosition());
                    documentAndTermDataForRankings.add(data);
                }
            }
        }

        postingFileReader.close();

        return documentAndTermDataForRankings;
    }
}
