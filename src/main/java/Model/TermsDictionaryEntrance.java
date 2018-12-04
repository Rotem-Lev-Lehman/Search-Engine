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

    public static ADictionaryEntrance ParseDictionaryRowAsMyKind(String dictionaryRow) {
        //String term, int docFreq, don't need:"int postingPtr"
        String[] split = dictionaryRow.split(";");
        return new TermsDictionaryEntrance(split[0], Integer.parseInt(split[1]), 0);
    }

    @Override
    public String toString() {
        //String term, int docFreq, don't need:"int postingPtr"
        return getTerm() + ';' + getDocFreq() + '\n';
    }
}
