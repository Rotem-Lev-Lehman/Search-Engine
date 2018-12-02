package Model;

import java.util.List;

public class MyTuple {//<Document, List<Term>>
    private Document document;
    private List<Term> terms;

    /**
     * @param document
     * @param terms
     * Constructor for Tuple , Tuple is a list that can NOT be changed after made but can be read.
     */
    public MyTuple(Document document, List<Term> terms) {
        this.document = document;
        this.terms = terms;
    }

    /**
     * @return Getter for the Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Setter for the Document
     */
    public void setDocument(Document document) {
        this.document = document;
    }


    /**
     * @return Getter for the Terms
     */
    public List<Term> getTerms() {
        return terms;
    }

    /**
     * Setter for the Terms
     */
    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }
}
