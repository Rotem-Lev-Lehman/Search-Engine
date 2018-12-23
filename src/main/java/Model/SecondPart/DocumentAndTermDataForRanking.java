package Model.SecondPart;

import Model.ADictionaryEntrance;
import Model.DocumentsDictionaryEntrance;
import Model.EntranceRow;

public class DocumentAndTermDataForRanking {
    DocumentsDictionaryEntrance documentData;
    ADictionaryEntrance termData;
    EntranceRow termInDocumentData;
    int position;

    public DocumentAndTermDataForRanking(DocumentsDictionaryEntrance documentData, ADictionaryEntrance termData, EntranceRow termInDocumentData, int position) {
        this.documentData = documentData;
        this.termData = termData;
        this.termInDocumentData = termInDocumentData;
        this.position = position;
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
}
