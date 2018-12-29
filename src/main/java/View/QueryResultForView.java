package View;

import Model.DocumentsDictionaryEntrance;
import Model.IdentityAndScore;

public class QueryResultForView{
    private String queryID;
    private Integer rank;
    private String documentID;

    private String e1;
    private Double s1;
    private String e2;
    private Double s2;
    private String e3;
    private Double s3;
    private String e4;
    private Double s4;
    private String e5;
    private Double s5;

    public QueryResultForView(String queryID, Integer rank, DocumentsDictionaryEntrance document){
        this.queryID = queryID;
        this.rank = rank;
        this.documentID = document.getDocNo();

        String dontExist = "---------";

        IdentityAndScore[] identityAndScores = document.getTopFiveBigWords();

        int curr = 0;
        e1 = identityAndScores[curr].getTerm();
        if(e1 == null)
            e1 = dontExist;
        s1 = identityAndScores[curr].getScore();
        curr++;

        e2 = identityAndScores[curr].getTerm();
        if(e2 == null)
            e2 = dontExist;
        s2 = identityAndScores[curr].getScore();
        curr++;

        e3 = identityAndScores[curr].getTerm();
        if(e3 == null)
            e3 = dontExist;
        s3 = identityAndScores[curr].getScore();
        curr++;

        e4 = identityAndScores[curr].getTerm();
        if(e4 == null)
            e4 = dontExist;
        s4 = identityAndScores[curr].getScore();
        curr++;

        e5 = identityAndScores[curr].getTerm();
        if(e5 == null)
            e5 = dontExist;
        s5 = identityAndScores[curr].getScore();
    }

    public String getQueryID() {
        return queryID;
    }

    public void setQueryID(String queryID) {
        this.queryID = queryID;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getE1() {
        return e1;
    }

    public void setE1(String e1) {
        this.e1 = e1;
    }

    public Double getS1() {
        return s1;
    }

    public void setS1(Double s1) {
        this.s1 = s1;
    }

    public String getE2() {
        return e2;
    }

    public void setE2(String e2) {
        this.e2 = e2;
    }

    public Double getS2() {
        return s2;
    }

    public void setS2(Double s2) {
        this.s2 = s2;
    }

    public String getE3() {
        return e3;
    }

    public void setE3(String e3) {
        this.e3 = e3;
    }

    public Double getS3() {
        return s3;
    }

    public void setS3(Double s3) {
        this.s3 = s3;
    }

    public String getE4() {
        return e4;
    }

    public void setE4(String e4) {
        this.e4 = e4;
    }

    public Double getS4() {
        return s4;
    }

    public void setS4(Double s4) {
        this.s4 = s4;
    }

    public String getE5() {
        return e5;
    }

    public void setE5(String e5) {
        this.e5 = e5;
    }

    public Double getS5() {
        return s5;
    }

    public void setS5(Double s5) {
        this.s5 = s5;
    }
}
