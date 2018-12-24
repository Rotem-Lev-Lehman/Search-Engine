package Model.SecondPart;

import javax.print.Doc;
import java.util.*;

public class Ranker {

    private List<DocRank> allRankedDocs;
    private List<String> sortedRankedDocsToReturn;
    private double k;
    private double b;



    public List<String> getRankedDocs(ArrayList<DocumentAndTermDataForRanking> DocsOb, int numOfDocToRank) {
        k=1.5;
        b=0.75;
        int count = 0;
        allRankedDocs= new ArrayList<>();
        // rank = Calc bm 25 for each doc and give it 90% percent and then 10% percent for the distance between words.
        // to sort the docs from their rank
        // return the doc numbers sorted (max 50)
        //ADD YOUR CODE HERE
        double AvgDl = getAvgDl(DocsOb, numOfDocToRank);
        for (int i = 0 ; i < numOfDocToRank ; i++){
            double bm25=0;
            double score;
            int docSize = DocsOb.get(i).getDocSizeSentFromSearcher();
            List<Integer> queryWordsTF = DocsOb.get(i).getWordsTFSentFromSearcher();
            List<Integer> queryWordsIDF = DocsOb.get(i).getWordsIDFSentFromSeacher();

            int querySize = DocsOb.get(i).getQuerySizeSentFromSearcher();
            for (int j = 0 ; j < querySize ; j++){
                double wordTF = queryWordsTF.get(j).doubleValue();
                double wordIDF = queryWordsIDF.get(j).doubleValue();
                double tmp = wordIDF*(wordTF*(k+1))/(wordTF+(k*(1-b+((b*docSize)/AvgDl))));
                bm25=bm25+tmp;
            }

            score = bm25; //more to add for the score calculation;
            DocRank docScore = DocRank(DocsOb.get(i).getDocIDSentFromSearcher(), score);
            allRankedDocs.add(docScore);
        }
        Collections.sort(allRankedDocs, new toSort());
        while (count < 50 & allRankedDocs.get(count)!=null){
            sortedRankedDocsToReturn.add(allRankedDocs.get(count).getDocID());
        }
        return sortedRankedDocsToReturn;
    }



    public double getAvgDl(List<DocumentAndTermDataForRanking> DocsOb, int numOfDocToRank) {
        double AvgDl = 0;
        for (int i = 0; i < numOfDocToRank; i++) {
            AvgDl=AvgDl+DocsOb.get(i).getDocSize();
        }
        AvgDl=AvgDl/numOfDocToRank;
        return AvgDl;
    }

    public class toSort implements Comparator<Object>{
        @Override
        public int compare(Object o1, Object o2) {
            double first = ((DocRank)o1).getRank();
            double second = ((DocRank)o2).getRank();
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
        private String docID;
        private double score;

        public DocRank(String docID, double score) {
            this.docID = docID;
            this.score = score;
        }

        public String getDocID() {
            return docID;
        }

        public void setDocID(String docID) {
            this.docID = docID;
        }

        public double getRank() {
            return score;
        }

        public void setRank(double score) {
            this.score = score;
        }
    }
}
