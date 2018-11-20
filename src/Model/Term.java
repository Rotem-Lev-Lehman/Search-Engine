package Model;

import java.util.Objects;

/**
 * A class that represents a term
 */
public class Term {
    /**
     * The value of the term
     */
    private String value;
    private int numOfTokensParsed;
    private boolean endedWithHyphen;

    /** Constructor
     * @param value - The value of the term
     */
    public Term(String value) {
        this.value = value;
        this.numOfTokensParsed = 1;
        this.endedWithHyphen = false;
    }

    public Term(String value, int numOfTokensParsed){
        this.value = value;
        this.numOfTokensParsed = numOfTokensParsed;
        this.endedWithHyphen = false;
    }

    public Term(String value, int numOfTokensParsed, boolean endedWithHyphen){
        this.value = value;
        this.numOfTokensParsed = numOfTokensParsed;
        this.endedWithHyphen = endedWithHyphen;
    }

    /** Gets the value of the term
     * @return The value of the term
     */
    public String getValue() {
        return value;
    }

    /** Sets the value of the term
     * @param value - The value of the term
     */
    public void setValue(String value) {
        this.value = value;
    }

    public int getNumOfTokensParsed() {
        return numOfTokensParsed;
    }

    public void setNumOfTokensParsed(int numOfTokensParsed) {
        this.numOfTokensParsed = numOfTokensParsed;
    }

    public boolean isEndedWithHyphen() {
        return endedWithHyphen;
    }

    public void setEndedWithHyphen(boolean endedWithHyphen) {
        this.endedWithHyphen = endedWithHyphen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return Objects.equals(value, term.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
