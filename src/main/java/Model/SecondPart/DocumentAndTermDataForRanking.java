package Model.SecondPart;

import Model.ADictionaryEntrance;
import Model.DocumentsDictionaryEntrance;
import Model.EntranceRow;

public class DocumentAndTermDataForRanking {
    private DocumentsDictionaryEntrance documentData;
    private ADictionaryEntrance termData;
    private EntranceRow termInDocumentData;
    private int position;
    private String queryID;
    private int numOfSubQuery;

    public DocumentAndTermDataForRanking(DocumentsDictionaryEntrance documentData, ADictionaryEntrance termData, EntranceRow termInDocumentData, int position, String queryID, int numOfSubQuery) {
        this.documentData = documentData;
        this.termData = termData;
        this.termInDocumentData = termInDocumentData;
        this.position = position;
        this.queryID = queryID;
        this.numOfSubQuery = numOfSubQuery;
    }

    public DocumentsDictionaryEntrance getDocumentData() {
        return documentData;
    }

    public void setDocumentData(DocumentsDictionaryEntrance documentData) {
        this.documentData = documentData;
    }

    public ADictionaryEntrance getTermData() {
        return termData;
    }

    public void setTermData(ADictionaryEntrance termData) {
        this.termData = termData;
    }

    public EntranceRow getTermInDocumentData() {
        return termInDocumentData;
    }

    public void setTermInDocumentData(EntranceRow termInDocumentData) {
        this.termInDocumentData = termInDocumentData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getQueryID() {
        return queryID;
    }

    public void setQueryID(String queryID) {
        this.queryID = queryID;
    }

    public int getNumOfSubQuery() {
        return numOfSubQuery;
    }

    public void setNumOfSubQuery(int numOfSubQuery) {
        this.numOfSubQuery = numOfSubQuery;
    }
}
