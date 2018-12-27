package Model.SecondPart;

import Model.Document;
import Model.DocumentsDictionaryEntrance;

import java.util.ArrayList;
import java.util.List;

public class MyQuery {
    private Document document;
    private List<String> citiesRelevant;
    private String id;
    private List<SubQuery> subQueries;
    private int nextSubQueryNum;
    private List<DocumentsDictionaryEntrance> retrievedDocuments;

    public MyQuery(String text, List<String> citiesRelevant, String id){
        this.document = new Document();
        document.setTEXT(text);
        this.citiesRelevant = citiesRelevant;
        document.setCity(this.citiesRelevant);
        this.id = id;
        subQueries = new ArrayList<>();
        nextSubQueryNum = 0;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocumentText(String text) {
        this.document.setTEXT(text);
    }

    public List<String> getCitiesRelevant() {
        return citiesRelevant;
    }

    public void setCitiesRelevant(List<String> citiesRelevant) {
        this.citiesRelevant = citiesRelevant;
        document.setCity(this.citiesRelevant);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubQueries(List<SubQuery> subQueries) {
        this.subQueries = subQueries;
    }

    public void addSubQuery(SubQuery subQuery){
        subQueries.add(subQuery);
    }

    public List<SubQuery> getSubQueries(){
        return subQueries;
    }

    public int getNextSubQueryNum(){
        int next = nextSubQueryNum;
        nextSubQueryNum++;
        return next;
    }

    public List<DocumentsDictionaryEntrance> getRetrievedDocuments() {
        return retrievedDocuments;
    }

    public void setRetrievedDocuments(List<DocumentsDictionaryEntrance> retrievedDocuments) {
        this.retrievedDocuments = retrievedDocuments;
    }
}
