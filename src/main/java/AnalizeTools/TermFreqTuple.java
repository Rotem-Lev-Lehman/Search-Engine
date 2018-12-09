package AnalizeTools;

import Model.ADictionaryEntrance;
import Model.TypeOfTerm;

public class TermFreqTuple implements Comparable{
    private String term;
    private int freq;

    public TermFreqTuple(String term, int freq){
        this.term = term;
        this.freq = freq;
    }

    public static TermFreqTuple CreateTermFreqTuple(ADictionaryEntrance dictionaryEntrance){
        TermFreqTuple tuple = new TermFreqTuple(dictionaryEntrance.getTerm(), dictionaryEntrance.getTotalTermFreq());
        return tuple;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof TermFreqTuple){
            TermFreqTuple other = (TermFreqTuple)o;
            int diff = other.freq - this.freq;
            if(diff == 0)
                return term.compareTo(other.term);
            else
                return diff;
        }
        return 0;
    }

    @Override
    public String toString() {
        return term + "," + freq + "\n";
    }
}
