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
        k = 1.25;
        b = 0.75;
        allRankedDocs = new ArrayList<>();
        sortedToReturn = new ArrayList<>();
    }

    public double calcIDF(int DF, int TotalNumOfDocs) {
        double IDF = Math.log(((double) TotalNumOfDocs - (double) DF + 0.5) / ((double) DF + 0.5)) / Math.log(2);
        return IDF;
    }

    public void Rank(MyQuery myQuery, double avgDl, int TotalNumOfDocs) {
        initRank();
        //double bm25;
        double distanceWordsWeight;
        double maxBM25 = 0;
        int nonSemanticTermsCount = 0;

        for(SubQuery subQuery : myQuery.getSubQueries()) {
            //List<DocumentAndTermDataForRanking> data = myQuery.getSubQueries().get(0).getData();
            for(Term term : subQuery.getTerms()){
                List<DocumentAndTermDataForRanking> data = subQuery.getData().get(term.getValue());
                if(data == null)
                    continue;

                if(term.isSemanticTerm())
                    nonSemanticTermsCount++;

                for(DocumentAndTermDataForRanking doc : data){
                    int MaxTF = doc.getDocumentData().getMaxTf();
                    int numOfUniqueWords = doc.getDocumentData().getUniqueWordsAmount();
                    int DF = doc.getTermData().getDocFreq();//maybe wrong to use

                    double docSize = ((double) MaxTF / 2.0) * (double) numOfUniqueWords;

                    double IDF = calcIDF(DF, TotalNumOfDocs);
                    double TF = (double) doc.getTermInDocumentData().getNormalizedTermFreq() * (double) MaxTF;
                    double bm25 = IDF * (TF * (k + 1.0) / (TF + (k * (1.0 - b + b * (docSize / avgDl)))));

                    List<Integer> currPos = doc.getTermInDocumentData().getPositions();

                    DocRank docScore = new DocRank(doc.getDocumentData());
                    docScore.currBM25 = bm25;
                    docScore.addToPositions(currPos);
                    docScore.setDocLength(docSize);
                    if(term.isSemanticTerm())
                        docScore.addNonSemanticTerm();
                    if (!allRankedDocs.contains(docScore)) {
                        allRankedDocs.add(docScore);
                    } else {
                        addScoreToExistsScore(docScore, currPos);
                    }

                }
            }
        }

        for(DocRank docRank : allRankedDocs){
            docRank.normalizeAmountOfNonSemanticTerms(nonSemanticTermsCount);
            if(docRank.currBM25 > maxBM25)
                maxBM25 = docRank.currBM25;
        }
        for(DocRank docRank : allRankedDocs){
            docRank.normalizeBM25(maxBM25);
            docRank.calculatePositions();
            docRank.calculateScore();
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

    private void addScoreToExistsScore(DocRank docRank, List<Integer> pos) {
        for (int i=0 ; i < allRankedDocs.size(); i++){
            if(allRankedDocs.get(i).equals(docRank)){
                allRankedDocs.get(i).addToBM25(docRank.getCurrBM25());
                allRankedDocs.get(i).addToPositions(pos);
                allRankedDocs.get(i).addAmountToNonSemanticTermCount(docRank.amountOfNonSemanticTerms);
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
        private double currBM25;
        private double posScore;
        private List<List<Integer>> positions;
        private double docLength;
        private double score;
        private double amountOfNonSemanticTerms;

        public DocRank(DocumentsDictionaryEntrance documentsDictionaryEntrance) {
            this.documentsDictionaryEntrance = documentsDictionaryEntrance;
            this.score = 0;
            currBM25 = 0;
            posScore = 0;
            amountOfNonSemanticTerms = 0;
            positions = new ArrayList<>();
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

        public double getCurrBM25() {
            return currBM25;
        }

        public void setCurrBM25(double currBM25) {
            this.currBM25 = currBM25;
        }

        public void addToBM25(double bm25){
            currBM25 += bm25;
        }

        public void calculateScore(){
            //System.out.println("bm25 = " + currBM25 + ", pos score = " + posScore);
            score = currBM25 * 0.25 + amountOfNonSemanticTerms * 0.7 + posScore * 0.05;
        }

        public void addToPositions(List<Integer> pos){
            positions.add(pos);
        }

        public double getDocLength() {
            return docLength;
        }

        public void setDocLength(double docLength) {
            this.docLength = docLength;
        }

        public void addNonSemanticTerm(){
            amountOfNonSemanticTerms++;
        }

        public void addAmountToNonSemanticTermCount(double amount){
            amountOfNonSemanticTerms += amount;
        }

        public void normalizeAmountOfNonSemanticTerms(int amountOfNonSemanticTerms){
            this.amountOfNonSemanticTerms /= (double)amountOfNonSemanticTerms;
        }

        /*
                public void addToPositions(double pos){
                    currPositions += pos;
                }

                public double getCurrPositions() {
                    return currPositions;
                }

                public void setCurrPositions(double currPositions) {
                    this.currPositions = currPositions;
                }
                */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DocRank docRank = (DocRank) o;
            return Objects.equals(documentsDictionaryEntrance, docRank.documentsDictionaryEntrance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(documentsDictionaryEntrance);
        }

        public void calculatePositions() {
            double sum = 0;
            for(int i = 0; i < positions.size() - 1; i++){
                for(int j = i + 1; j < positions.size(); j++){
                    int min = Integer.MAX_VALUE;
                    for(int posI : positions.get(i)){
                        for(int posJ : positions.get(j)){
                            int curr = Math.abs(posI - posJ);
                            if(curr < min)
                                min = curr;
                        }
                    }
                    sum += min / docLength;
                }
            }
            sum /= positions.size();

            posScore = sum;
        }

        public void normalizeBM25(double maxBM25) {
            currBM25 /= maxBM25;
        }
    }
}
