package Model;

import java.io.Serializable;

public class TermsDictionaryEntrance extends ADictionaryEntrance implements Serializable {

    /**
     * @param term
     * @param docFreq
     * @param totalTermFreq
     * @param postingPtr
     * Constructor for TermsDictionaryEntrance
     */
    public TermsDictionaryEntrance(String term, int docFreq, int totalTermFreq, int postingPtr) {
        super(term, docFreq, totalTermFreq, postingPtr);
    }

    /**
     * @param dictionaryRow
     * @return return TermDictionaryEntrance from a string representing the dictionary row entrance.
     */
    public static ADictionaryEntrance ParseDictionaryRowAsMyKind(String dictionaryRow) {
        //String term, int docFreq, int totalTermFreq, don't need:"int postingPtr"
        String[] split = dictionaryRow.split(";");
        return new TermsDictionaryEntrance(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), 0);
    }

    @Override
    public String toString() {
        //String term, int docFreq, int totalTermFreq, don't need:"int postingPtr"
        return getTerm() + ';' + getDocFreq() + ';' + getTotalTermFreq() + '\n';
    }
}
