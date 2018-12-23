package Model.SecondPart;

import Model.Document;

public class MyQuery {
    private Document document;
    private String[] citiesRelevant;

    public MyQuery(String text){
        this.document = new Document();
        document.setTEXT(text);
        citiesRelevant = null;
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
}
