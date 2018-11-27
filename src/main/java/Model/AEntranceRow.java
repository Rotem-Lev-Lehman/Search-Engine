package Model;
//AEntranceRow
public abstract class AEntranceRow {
    private String docNo;
    private String fileName;
    private int termFreqInDoc;

    public AEntranceRow(String docNo ,String fileName ,int termFreqInDoc){
        this.docNo = docNo;
        this.fileName = fileName;
        this.termFreqInDoc = termFreqInDoc;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTermFreqInDoc() {
        return termFreqInDoc;
    }

    public void setTermFreqInDoc(int termFreqInDoc) {
        this.termFreqInDoc = termFreqInDoc;
    }

}
