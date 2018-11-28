package Model;

public abstract class ADictionaryEntrance {
    private String term;
    private int docFreq;
    private int postingPtr;

    public ADictionaryEntrance(String term, int docFreq, int postingPtr){
        this.term = term;
        this.docFreq = docFreq;
        this.postingPtr = postingPtr;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }

    public void addOneToDocFreq(){
        docFreq++;
    }

    public int getPostingPtr() {
        return postingPtr;
    }

    public void setPostingPtr(int postingPtr) {
        this.postingPtr = postingPtr;
    }

    public void addToDocFreq(int df){
        docFreq+=df;
    }
}
