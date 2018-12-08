package Model;

import java.util.*;
import java.util.concurrent.Semaphore;

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

    public void addDocumentToIndex(List<Term> terms, String DocNo, String Filename, CityInfo info, int length, Semaphore maxTfCalculatorSemaphore, Semaphore maxTfUpdateSemaphore, Object tfLock, MyInteger tfDocument, MyInteger uniqueTermsNum) {
        List<Integer> positions;
        List<TupleEntranceRowAndTerm> entranceRows = new LinkedList<TupleEntranceRowAndTerm>();
        int maxTf = 0;
        for (int i = 0; i < terms.size(); i++) {
            Term current = terms.get(i);
            if (current == null)
                continue;
            positions = new ArrayList<>();
            positions.add(current.getPosition());
            for (int j = i + 1; j < terms.size(); j++) {
                Term other = terms.get(j);
                if (current.equals(other)) {
                    positions.add(other.getPosition());
                    terms.set(j, null);
                }
            }
            if (maxTf < positions.size())
                maxTf = positions.size();

            Collections.sort(positions); //make sure that the positions are sorted

            EntranceRow entranceRow = new EntranceRow(DocNo, Filename, positions.size(), positions);
            entranceRows.add(new TupleEntranceRowAndTerm(entranceRow, terms.get(i)));
        }

        //check what is the max tf in the whole document:
        synchronized (tfLock){
            if(tfDocument.getValue() < maxTf)
                tfDocument.setValue(maxTf);

            //update uniqueTermsNum:
            uniqueTermsNum.setValue(uniqueTermsNum.getValue() + entranceRows.size()); //update this for the documents usage
        }

        //tell the main thread that I have finished calculating:
        maxTfUpdateSemaphore.release();

        //wait for all of the threads to calculate it too:
        try {
            maxTfCalculatorSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //update the maxTf to be the calculated one:
        synchronized (tfLock){
            maxTf = tfDocument.getValue();
        }

        synchronized (lock) {
            for (TupleEntranceRowAndTerm tuple : entranceRows) {
                tuple.getEntranceRow().setNormalizedTermFreq((double) tuple.getEntranceRow().getTermFreqInDoc() / (double) maxTf); // normalized by max tf in doc
                //tuple.getEntranceRow().setNormalizedTermFreq((double) tuple.getEntranceRow().getTermFreqInDoc() / (double) length); // normalized by length of doc
                ADictionaryEntrance dictionaryEntrance = dictionary.getEntrance(tuple.getTerm());
                if (dictionaryEntrance == null) {
                    // create a new one
                    //Add to posting
                    ArrayList<EntranceRow> arrayList = new ArrayList<EntranceRow>();
                    arrayList.add(tuple.getEntranceRow());
                    PostingRow postingRow = new PostingRow(arrayList);
                    int ptr = posting.createNewPostingRow(postingRow);

                    //Add to dictionary
                    ADictionaryEntrance dicEntrance = getRightDictionaryEntrance(tuple.getTerm(), 1, ptr, info, tuple.getEntranceRow().getTermFreqInDoc());
                    dictionary.addEntrance(dicEntrance);
                } else {
                    // add to an existing one
                    int ptr = dictionaryEntrance.getPostingPtr();
                    dictionaryEntrance.addOneToDocFreq(); //df++
                    dictionaryEntrance.addToTotalTermFreq(tuple.getEntranceRow().getTermFreqInDoc());

                    PostingRow postingRow = posting.getPostingRow(ptr);
                    postingRow.getEntranceRows().add(tuple.getEntranceRow());
                }
            }
        }
    }

    protected abstract ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr, CityInfo info, int tf);

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

    public void SortAll(){
        ArrayList<PostingRow> postingRows = new ArrayList<PostingRow>();
        for(Map.Entry<String, ADictionaryEntrance> tuple : dictionary.getMap().entrySet()){
            postingRows.add(posting.getPostingRow(tuple.getValue().getPostingPtr()));
        }
        posting.setPostingList(postingRows);

        //dictionary is already sorted :)
    }
}
