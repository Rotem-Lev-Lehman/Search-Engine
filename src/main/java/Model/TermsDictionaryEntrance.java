package Model;

import java.io.Serializable;

public class TermsDictionaryEntrance extends ADictionaryEntrance implements Serializable {

    /**
     * @param term
     * @param docFreq
     * @param postingPtr
     * Constructor for TermsDictionaryEntrance
     */
    public TermsDictionaryEntrance(String term, int docFreq, int postingPtr) {
        super(term, docFreq, postingPtr);
    }
}
