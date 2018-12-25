package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;
import Model.Term;

import javax.print.Doc;
import java.util.*;

public class Ranker {

    private List<DocRank> allRankedDocs;
    private List<DocumentsDictionaryEntrance> sortedToReturn;
    private double k;
    private double b;

    public void initRank() {
        k = 1.5;
        b = 0.75;
        allRankedDocs = new ArrayList<>();
        sortedToReturn = new ArrayList<>();
    }

    public double calcIDF(int DF, int TotalNumOfDocs) {
        double IDF = Math.log(((double) TotalNumOfDocs - (double) DF + 0.5) / ((double) DF + 0.5));
        return IDF;
    }

    public void Rank(MyQuery myQuery, double avgDl, int TotalNumOfDocs) {
        initRank();
        double bm25;
        double distanceWordsWeight;
        double score;

        List<DocumentAndTermDataForRanking> data = myQuery.getSubQueries().get(0).getData();
        for (int i = 0; i < data.size(); i++) {
            bm25=0.0;
            int MaxTF = data.get(i).getDocumentData().getMaxTf();
            int numOfUniqueWords = data.get(i).getDocumentData().getUniqueWordsAmount();
            int DF = data.get(i).getTermData().getDocFreq();//maybe wrong to use
            //int TF = data.get(i).getTermData().getTotalTermFreq();
//--------------------------------------------------------------------------------------ADDED
            List<Term> TermsPos= myQuery.getSubQueries().get(0).getTerms();
            int lastPos;
            int pos=0;
            for (int z= 0 ; z < TermsPos.size() ; z++) {
                lastPos = myQuery.getSubQueries().get(0).getTerms().get(z).getPosition();
                if (!(Math.abs(pos-lastPos)<4)) {
                    pos = pos + Math.abs(pos - lastPos);
                }
            }
//---------------------------------------------------------------------------------------
            double docSize = ((double)MaxTF / 2.0) * (double)numOfUniqueWords;
            for (int s = 0; s < numOfUniqueWords; s++) {
                double IDF = calcIDF(DF, TotalNumOfDocs);
                double TF = (double)data.get(i).getTermInDocumentData().getNormalizedTermFreq()*(double)MaxTF;
                bm25 += IDF * (TF * (k + 1.0) / (TF + (k * (1.0 - b + b * (docSize / avgDl)))));
                //distanceWordsWeight += data.get(k)
            }
            //Now we also calc for positions
            double posWeight = (docSize/(double)pos);
            score = bm25*0.85+posWeight*0.15; // for now
            DocRank docScore = new DocRank((DocumentsDictionaryEntrance)myQuery.getSubQueries().get(0).getData().get(i).getDocumentData(),score);
            if(!allRankedDocs.contains(docScore)) {
                allRankedDocs.add(docScore);
            }
            else{
                addScoreToExistsScore(docScore);
            }
        }

        Collections.sort(allRankedDocs, new toSort());
        int count = Math.min(50, allRankedDocs.size());
        //int count = allRankedDocs.size();
        HashSet<String> seenDocs = new HashSet<>();
        for(int i = 0; i < count; i++) {
            if(seenDocs.contains(allRankedDocs.get(i).getDocumentsDictionaryEntrance().getDocNo().replace(" ", "")))
                continue;
            seenDocs.add(allRankedDocs.get(i).getDocumentsDictionaryEntrance().getDocNo().replace(" ", ""));
            sortedToReturn.add(allRankedDocs.get(i).getDocumentsDictionaryEntrance());
        }

        myQuery.setRetrievedDocuments(sortedToReturn);
    }

    private void addScoreToExistsScore(DocRank docRank) {
        for (int i=0 ; i < allRankedDocs.size(); i++){
            if(allRankedDocs.get(i)==docRank){
                allRankedDocs.get(i).score+=docRank.getScore();
                return;
            }
        }
    }


    public class toSort implements Comparator<Object>{
        @Override
        public int compare(Object o1, Object o2) {
            double first = ((DocRank)o1).getScore();
            double second = ((DocRank)o2).getScore();
            if (first<second){
                return 1;
            }
            else if (second<first){
                return -1;
            }
            else{
                return 0;
            }
        }

    }

    public class DocRank{
        private DocumentsDictionaryEntrance documentsDictionaryEntrance;
        private double score;

        public DocRank(DocumentsDictionaryEntrance documentsDictionaryEntrance, double score) {
            this.documentsDictionaryEntrance = documentsDictionaryEntrance;
            this.score = score;
        }

        public DocumentsDictionaryEntrance getDocumentsDictionaryEntrance() {
            return documentsDictionaryEntrance;
        }

        public void setDocumentsDictionaryEntrance(DocumentsDictionaryEntrance documentsDictionaryEntrance) {
            this.documentsDictionaryEntrance = documentsDictionaryEntrance;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
