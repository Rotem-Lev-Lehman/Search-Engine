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


    /**
     * @param docNo
     * @param fileName
     * @param termFreqInDoc
     * @param positions
     * constructor for the EntranceRow
     */
    public EntranceRow(String docNo , String fileName , int termFreqInDoc, List<Integer> positions){
        this.docNo = docNo;
        this.fileName = fileName;
        this.termFreqInDoc = termFreqInDoc;
        this.positions = positions;
    }


    /**
     * @return Getter for the DocNo
     */
    public String getDocNo() {
        return docNo;
    }

    /**
     * Setter for the DocNo
     */
    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    /**
     * @return Getter for the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter for the fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Getter for the termFreqInDoc
     */
    public int getTermFreqInDoc() {
        return termFreqInDoc;
    }

    /**
     * Setter for the termFreqInDoc
     */
    public void setTermFreqInDoc(int termFreqInDoc) {
        this.termFreqInDoc = termFreqInDoc;
    }

    /**
     * @return Getter for the normalizedTermFreq
     */
    public double getNormalizedTermFreq() {
        return normalizedTermFreq;
    }

    /**
     * Setter for the normalizedTermFreq
     */
    public void setNormalizedTermFreq(double normalizedTermFreq) {
        this.normalizedTermFreq = normalizedTermFreq;
    }

    /**
     * @return Getter for the positions (List<Integers>)
     */
    public List<Integer> getPositions() {
        return positions;
    }

    /**
     * Setter for the positions (List<Integers>)
     */
    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }


    /**
     * comparator between this "this" Entrance row and an Object if is instance of EntranceRow
     * @param o
     * @return -1 if "this" normalizedTermFreq is smaller the other normalizedTermFreq, 1 if Bigger , or 0 otherwise(equals or couldn't compare).
     */
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
