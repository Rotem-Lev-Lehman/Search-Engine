package Model;

import java.io.Serializable;

public class TermsDictionaryEntrance extends ADictionaryEntrance implements Serializable {
    public TermsDictionaryEntrance(String term, int docFreq, int postingPtr) {
        super(term, docFreq, postingPtr);
    }
}
