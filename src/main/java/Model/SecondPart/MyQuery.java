package Model.SecondPart;

import Model.Document;
import Model.Term;

import java.util.ArrayList;
import java.util.List;

public class MyQuery {
    private Document document;
    private String[] citiesRelevant;
    private String id;
    private List<SubQuery> subQueries;
    private int nextSubQueryNum;

    public MyQuery(String text, String id){
        this.document = new Document();
        document.setTEXT(text);
        citiesRelevant = null;
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

    public String[] getCitiesRelevant() {
        return citiesRelevant;
    }

    public void setCitiesRelevant(String[] citiesRelevant) {
        this.citiesRelevant = citiesRelevant;
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
}
