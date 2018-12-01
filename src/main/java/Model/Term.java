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
    private TypeOfTerm type;

    /** Constructor
     * @param value - The value of the term
     */
    public Term(String value, TypeOfTerm type) {
        this.value = value;
        this.numOfTokensParsed = 1;
        this.endedWithHyphen = false;
        this.type = type;
    }

    public Term(String value, TypeOfTerm type, int numOfTokensParsed){
        this.value = value;
        this.numOfTokensParsed = numOfTokensParsed;
        this.endedWithHyphen = false;
        this.type = type;
    }

    public Term(String value, TypeOfTerm type, int numOfTokensParsed, boolean endedWithHyphen){
        this.value = value;
        this.numOfTokensParsed = numOfTokensParsed;
        this.endedWithHyphen = endedWithHyphen;
        this.type = type;
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

    public TypeOfTerm getType() {
        return type;
    }

    public void setType(TypeOfTerm type) {
        this.type = type;
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
