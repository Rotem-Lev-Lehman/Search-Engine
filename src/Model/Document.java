package Model;

import java.util.Date;

/**
 * A class that represents a Document
 */
public class Document {
    /**
     * DOCNO - The id of the Document
     */
    private String DOCNO;
    /**
     * DATE1 - The date that the Document was published
     */
    private Date DATE1;
    /**
     * TI - The headline of the Document
     */
    private String TI;
    /**
     * TEXT - The content of the Document
     */
    private String TEXT;

    /**
     * An empty constructor
     */
    public Document() {
        this.DOCNO = null;
        this.DATE1 = null;
        this.TI = null;
        this.TEXT = null;
    }

    /**
     * A full constructor
     * @param DOCNO - The id of the Document
     * @param DATE1 - The date that the Document was published
     * @param TI - The headline of the Document
     * @param TEXT - The content of the Document
     */
    public Document(String DOCNO, Date DATE1, String TI, String TEXT) {
        this.DOCNO = DOCNO;
        this.DATE1 = DATE1;
        this.TI = TI;
        this.TEXT = TEXT;
    }

    /** A getter for the DOCNO
     * @return The Document's DOCNO
     */
    public String getDOCNO() {
        return DOCNO;
    }

    /** A setter for the DOCNO
     * @param DOCNO - The id of the Document
     */
    public void setDOCNO(String DOCNO) {
        this.DOCNO = DOCNO;
    }

    /** A getter for the DATE1
     * @return The Document's DATE1
     */
    public Date getDATE1() {
        return DATE1;
    }

    /** A setter for the DATE1
     * @param DATE1 - The date that the Document was published
     */
    public void setDATE1(Date DATE1) {
        this.DATE1 = DATE1;
    }

    /** A getter for the TI
     * @return The Document's TI
     */
    public String getTI() {
        return TI;
    }

    /** A setter for the TI
     * @param TI - The headline of the Document
     */
    public void setTI(String TI) {
        this.TI = TI;
    }

    /** A getter for the TEXT
     * @return The Document's TEXT
     */
    public String getTEXT() {
        return TEXT;
    }

    /** A setter for the TEXT
     * @param TEXT - The content of the Document
     */
    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }
}
