package Model.SecondPart;

import Model.Term;
import Model.TypeOfTerm;

public class QuerysTerm {
    private String queryID;
    private int subQueryNum;
    private Term term;

    public QuerysTerm(Term term, String queryID, int subQueryNum) {
        this.queryID = queryID;
        this.subQueryNum = subQueryNum;
        this.term = term;
    }

    public String getQueryID() {
        return queryID;
    }

    public void setQueryID(String queryID) {
        this.queryID = queryID;
    }

    public int getSubQueryNum() {
        return subQueryNum;
    }

    public void setSubQueryNum(int subQueryNum) {
        this.subQueryNum = subQueryNum;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public TypeOfTerm getType(){
        return term.getType();
    }

    public String getValue(){
        return term.getValue();
    }
}
