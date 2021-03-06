package Model;

public abstract class ADictionaryEntrance {
    private String term;
    private int docFreq;
    private int totalTermFreq;
    private int postingPtr;

    /**
     * @param term
     * @param docFreq
     * @param totalTermFreq
     * @param postingPtr
     * Constructor for a ADictionaryEntrance
     */
    public ADictionaryEntrance(String term, int docFreq, int totalTermFreq, int postingPtr){
        this.term = term;
        this.docFreq = docFreq;
        this.totalTermFreq = totalTermFreq;
        this.postingPtr = postingPtr;
    }

    /**
     * @return Getter for the Term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @return Setter for the Term
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * @return Getter for the docFreq
     */
    public int getDocFreq() {
        return docFreq;
    }

    /**
     * @return Setter for the docFreq
     */
    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }

    /**
     * @return add 1 to the docFreq
     */
    public void addOneToDocFreq(){
        docFreq++;
    }

    /**
     * @return Getter for the postingPtr
     */
    public int getPostingPtr() {
        return postingPtr;
    }

    /**
     * @return Setter for the postingPtr
     */
    public void setPostingPtr(int postingPtr) {
        this.postingPtr = postingPtr;
    }

    /**
     * @param df
     * add df to the docFreq
     */
    public void addToDocFreq(int df){
        docFreq+=df;
    }

    public int getTotalTermFreq() {
        return totalTermFreq;
    }

    public void setTotalTermFreq(int totalTermFreq) {
        this.totalTermFreq = totalTermFreq;
    }

    public void addToTotalTermFreq(int tf){
        this.totalTermFreq += tf;
    }
}
