package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AIndex {
    private MyDictionary dictionary;
    private Posting posting;

    public AIndex() {
        dictionary = new MyDictionary();
        posting = new Posting();
    }

    public void addDocumentToIndex(Term[] terms, Document document){
        List<Integer> positions;
        for (int i = 0; i < terms.length; i++) {
            if(terms[i] == null)
                continue;
            positions = new ArrayList<>();
            positions.add(i);
            for (int j = i + 1; j < terms.length; j++){
                if(terms[i].equals(terms[j])) {
                    positions.add(j);
                    terms[j] = null;
                }
            }
            EntranceRow entranceRow = new EntranceRow(document.getDOCNO(),document.getFilename(),positions.size(),positions);

            ADictionaryEntrance dictionaryEntrance = dictionary.getEntrance(terms[i]);
            if(dictionaryEntrance == null){
                // create a new one
                //Add to posting
                ArrayList<EntranceRow> arrayList = new ArrayList<EntranceRow>();
                arrayList.add(entranceRow);
                PostingRow postingRow = new PostingRow(arrayList);
                int ptr = posting.createNewPostingRow(postingRow);

                //Add to dictionary
                ADictionaryEntrance dicEntrance = getRightDictionaryEntrance(terms[i], 1, ptr);
                dictionary.addEntrance(dicEntrance);
            }
            else{
                // add to an existing one
                int ptr = dictionaryEntrance.getPostingPtr();
                dictionaryEntrance.addOneToDocFreq(); //df++
                PostingRow postingRow = posting.getPostingRow(ptr);
                postingRow.getEntranceRows().add(entranceRow);
                Collections.sort(postingRow.getEntranceRows()); // maybe sort when saving the posting
            }
        }
    }

    protected abstract ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr);
}
