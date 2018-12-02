package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AIndex {
    private MyDictionary dictionary;
    private Posting posting;
    private volatile Object lock;
    private TypeOfIndex type;

    public AIndex() {
        dictionary = new MyDictionary();
        posting = new Posting();
        lock = new Object();
        type = TypeOfIndex.SmallLetters;
    }

    public void ClearIndex(){
        synchronized (lock) {
            dictionary = new MyDictionary();
            posting = new Posting();
        }
    }

    public void addDocumentToIndex(List<Term> terms, String DocNo, String Filename) {
        List<Integer> positions;
        List<TupleEntranceRowAndTerm> entranceRows = new LinkedList<TupleEntranceRowAndTerm>();
        int maxTf = 0;
        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i) == null)
                continue;
            positions = new ArrayList<>();
            positions.add(i);
            for (int j = i + 1; j < terms.size(); j++) {
                if (terms.get(i).equals(terms.get(j))) {
                    positions.add(j);
                    terms.set(j, null);
                }
            }
            if (maxTf < positions.size())
                maxTf = positions.size();

            EntranceRow entranceRow = new EntranceRow(DocNo, Filename, positions.size(), positions);
            entranceRows.add(new TupleEntranceRowAndTerm(entranceRow, terms.get(i)));
        }
        synchronized (lock) {
            for (TupleEntranceRowAndTerm tuple : entranceRows) {
                tuple.getEntranceRow().setNormalizedTermFreq((double) tuple.getEntranceRow().getTermFreqInDoc() / (double) maxTf); // normalized by max tf in doc
                ADictionaryEntrance dictionaryEntrance = dictionary.getEntrance(tuple.getTerm());
                if (dictionaryEntrance == null) {
                    // create a new one
                    //Add to posting
                    ArrayList<EntranceRow> arrayList = new ArrayList<EntranceRow>();
                    arrayList.add(tuple.getEntranceRow());
                    PostingRow postingRow = new PostingRow(arrayList);
                    int ptr = posting.createNewPostingRow(postingRow);

                    //Add to dictionary
                    ADictionaryEntrance dicEntrance = getRightDictionaryEntrance(tuple.getTerm(), 1, ptr);
                    dictionary.addEntrance(dicEntrance);
                } else {
                    // add to an existing one
                    int ptr = dictionaryEntrance.getPostingPtr();
                    dictionaryEntrance.addOneToDocFreq(); //df++
                    PostingRow postingRow = posting.getPostingRow(ptr);
                    postingRow.getEntranceRows().add(tuple.getEntranceRow());
                }
            }
        }
    }

    protected abstract ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr);

    public boolean isEmpty(){
        boolean ans = false;
        synchronized (lock){
            if(dictionary.isEmpty() && posting.isEmpty())
                ans = true;
        }
        return ans;
    }

    public MyDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(MyDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Posting getPosting() {
        return posting;
    }

    public void setPosting(Posting posting) {
        this.posting = posting;
    }

    public TypeOfIndex getType() {
        return type;
    }

    public void setType(TypeOfIndex type) {
        this.type = type;
    }
}
