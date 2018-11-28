package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AIndex {
    private MyDictionary dictionary;
    private Posting posting;

    public AIndex() {
        dictionary = new MyDictionary();
        posting = new Posting();
    }

    public void addDocumentToIndex(List<Term> terms, Document document){
        List<Integer> positions;
        List<EntranceRow> entranceRows = new LinkedList<EntranceRow>();
        int maxTf = 0;
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i) == null)
                continue;
            positions = new ArrayList<>();
            positions.add(i);
            for (int j = i + 1; j < terms.size(); j++){
                if(terms.get(i).equals(terms.get(j))) {
                    positions.add(j);
                    terms.set(j,null);
                }
            }
            if(maxTf < positions.size())
                maxTf = positions.size();

            EntranceRow entranceRow = new EntranceRow(document.getDOCNO(),document.getFilename(),positions.size(),positions);
            entranceRows.add(entranceRow);

            ADictionaryEntrance dictionaryEntrance = dictionary.getEntrance(terms.get(i));
            if(dictionaryEntrance == null){
                // create a new one
                //Add to posting
                ArrayList<EntranceRow> arrayList = new ArrayList<EntranceRow>();
                arrayList.add(entranceRow);
                PostingRow postingRow = new PostingRow(arrayList);
                int ptr = posting.createNewPostingRow(postingRow);

                //Add to dictionary
                ADictionaryEntrance dicEntrance = getRightDictionaryEntrance(terms.get(i), 1, ptr);
                dictionary.addEntrance(dicEntrance);
            }
            else{
                // add to an existing one
                int ptr = dictionaryEntrance.getPostingPtr();
                dictionaryEntrance.addOneToDocFreq(); //df++
                PostingRow postingRow = posting.getPostingRow(ptr);
                postingRow.getEntranceRows().add(entranceRow);
            }
        }
        for (EntranceRow entrance : entranceRows) {
            entrance.setNormalizedTermFreq((double)entrance.getTermFreqInDoc()/(double)maxTf); // normalized by max tf in doc
        }
    }

    protected abstract ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr);

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
}
