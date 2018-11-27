package Model;

import java.util.List;

//EntranceRow
public class EntranceRow implements Comparable {
    private String docNo;
    private String fileName;
    private int termFreqInDoc;
    private List<Integer> positions;

    public EntranceRow(String docNo , String fileName , int termFreqInDoc, List<Integer> positions){
        this.docNo = docNo;
        this.fileName = fileName;
        this.termFreqInDoc = termFreqInDoc;
        this.positions = positions;
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

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }


}
