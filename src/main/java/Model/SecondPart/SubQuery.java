package Model.SecondPart;

import Model.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubQuery {
    private List<Term> terms;
    private int subQueryNum;
    private String mainQueryID;
    private Map<String, List<DocumentAndTermDataForRanking>> data;
//    private List<DocumentAndTermDataForRanking> data;

    public SubQuery(String mainQueryID) {
        this.terms = new ArrayList<>();
        this.mainQueryID = mainQueryID;
        this.data = new HashMap<>();
    }

    public SubQuery(SubQuery other){
        this.terms = new ArrayList<>();
        this.terms.addAll(other.terms);
        this.mainQueryID = other.mainQueryID;
        this.data = new HashMap<>();
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void addTerm(Term term){
        terms.add(term);
    }

    public void addTerms(List<Term> terms){
        this.terms.addAll(terms);
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

    public Map<String, List<DocumentAndTermDataForRanking>> getData() {
        return data;
    }

    public void addToData(String term, DocumentAndTermDataForRanking newData){
        List<DocumentAndTermDataForRanking> curr = data.get(term);
        if(curr == null) {
            curr = new ArrayList<>();
            curr.add(newData);
            data.put(term, curr);
        }
        else{
            curr.add(newData);
        }
    }
}
