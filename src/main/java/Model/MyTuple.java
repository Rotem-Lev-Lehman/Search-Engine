package Model;

import java.util.List;

public class MyTuple {//<Document, List<Term>>
    private String DocNo;
    private String Filename;
    private List<Term> terms;

    /**
     * @param document
     * @param terms
     * Constructor for Tuple , Tuple is a list that can NOT be changed after made but can be read.
     */
    public MyTuple(Document document, List<Term> terms) {
        this.DocNo = document.getDOCNO();
        this.Filename = document.getFilename();
        this.terms = terms;
    }

    /**
     * @return Getter for the Document
     */
    public String getDocNo() {
        return DocNo;
    }

    /**
     * Setter for the Document
     */
    public void setDocNo(String DocNo) {
        this.DocNo = DocNo;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
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
