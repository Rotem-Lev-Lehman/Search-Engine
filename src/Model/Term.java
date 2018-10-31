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

    /** Constructor
     * @param value - The value of the term
     */
    public Term(String value) {
        this.value = value;
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
