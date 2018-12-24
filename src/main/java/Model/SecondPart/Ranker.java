package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;

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
        double score;

        List<DocumentAndTermDataForRanking> data = myQuery.getSubQueries().get(0).getData();
        for (int i = 0; i < data.size(); i++) {
            score=0.0;
            bm25=0.0;
            int MaxTF = data.get(i).getDocumentData().getMaxTf();
            int numOfUniqueWords = data.get(i).getDocumentData().getUniqueWordsAmount();
            int DF = data.get(i).getTermData().getDocFreq();//maybe wrong to use
            //int TF = data.get(i).getTermData().getTotalTermFreq();

            for (int k = 0; k < numOfUniqueWords; k++) {
                double IDF = calcIDF(DF, TotalNumOfDocs);
                double docSize = ((double)MaxTF / 2.0) * (double)numOfUniqueWords;
                double TF = (double)data.get(i).getTermInDocumentData().getNormalizedTermFreq() * (double)MaxTF;
                bm25 = bm25 + IDF * (TF * (k + 1.0) / (TF + (k * (1.0 - b + b * (docSize / avgDl)))));

            }
            score = bm25; // for now
            DocRank docScore = new DocRank((DocumentsDictionaryEntrance)myQuery.getSubQueries().get(0).getData().get(i).getDocumentData(),score);
            allRankedDocs.add(docScore);
        }

        Collections.sort(allRankedDocs, new toSort());
        int count = Math.min(50, allRankedDocs.size());
        for(int i = 0; i < count; i++)
            sortedToReturn.add(allRankedDocs.get(i).getDocumentsDictionaryEntrance());

        myQuery.setRetrievedDocuments(sortedToReturn);
    }




    public class toSort implements Comparator<Object>{
        @Override
        public int compare(Object o1, Object o2) {
            double first = ((DocRank)o1).getScore();
            double second = ((DocRank)o2).getScore();
            if (first>second){
                return 1;
            }
            else if (second>first){
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
