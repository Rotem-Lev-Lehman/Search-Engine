package Model.SecondPart;

import Model.Term;

import java.util.ArrayList;
import java.util.List;

public class SubQuery {
    private List<Term> terms;
    private int subQueryNum;
    private String mainQueryID;
    private List<DocumentAndTermDataForRanking> data;

    public SubQuery(String mainQueryID) {
        this.terms = new ArrayList<>();
        this.mainQueryID = mainQueryID;
        this.data = new ArrayList<>();
    }

    public SubQuery(SubQuery other){
        this.terms = new ArrayList<>();
        this.terms.addAll(other.terms);
        this.mainQueryID = other.mainQueryID;
        this.data = new ArrayList<>();
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void addTerm(Term term){
        terms.add(term);
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public int getSubQueryNum() {
        return subQueryNum;
    }

    public List<QuerysTerm> getQueryTerms(){
        List<QuerysTerm> querysTerms = new ArrayList<>();
        for (Term term : terms){
            querysTerms.add(new QuerysTerm(term, mainQueryID, subQueryNum));
        }
        return querysTerms;
    }

    public String getMainQueryID() {
        return mainQueryID;
    }

    public void setMainQueryID(String mainQueryID) {
        this.mainQueryID = mainQueryID;
    }

    public void setSubQueryNum(int subQueryNum) {
        this.subQueryNum = subQueryNum;
    }

    public List<DocumentAndTermDataForRanking> getData() {
        return data;
    }

    public void addToData(DocumentAndTermDataForRanking newData){
        data.add(newData);
    }
}
