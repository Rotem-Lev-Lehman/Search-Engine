package Model;

import java.util.List;

public class MyTuple {//<Document, List<Term>>
    private Document document;
    private List<Term> terms;

    public MyTuple(Document document, List<Term> terms) {
        this.document = document;
        this.terms = terms;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }
}
