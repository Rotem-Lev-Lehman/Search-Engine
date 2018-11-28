package Model;

import java.io.Serializable;
import java.util.List;

//EntranceRow
public class EntranceRow implements Comparable, Serializable {
    private String docNo;
    private String fileName;
    private int termFreqInDoc;
    private double normalizedTermFreq;
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

    public double getNormalizedTermFreq() {
        return normalizedTermFreq;
    }

    public void setNormalizedTermFreq(double normalizedTermFreq) {
        this.normalizedTermFreq = normalizedTermFreq;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof EntranceRow) {
            EntranceRow other = (EntranceRow) o;
            if (this.normalizedTermFreq < other.normalizedTermFreq)
                return -1;
            if (this.normalizedTermFreq > other.normalizedTermFreq)
                return 1;
            //equal
            return this.docNo.compareTo(other.docNo);
        }
        return 0;
    }
}
