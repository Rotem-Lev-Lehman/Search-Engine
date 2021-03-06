package Model.SecondPart;

import Model.DocumentsDictionaryEntrance;
import Model.Term;

import java.util.*;

public class Ranker {

    private Map<DocumentsDictionaryEntrance, DocRank> allRankedDocs;
    private List<DocumentsDictionaryEntrance> sortedToReturn;
    private double k;
    private double b;

    public void initRank() {
        k = 1.25;
        b = 0.75;
        allRankedDocs = new HashMap<>();
        sortedToReturn = new ArrayList<>();
    }

    public double calcIDF(int DF, int TotalNumOfDocs) {
        double IDF = Math.log(((double) TotalNumOfDocs - (double) DF + 0.5) / ((double) DF + 0.5)) / Math.log(2);
        return IDF;
    }

    public void Rank(MyQuery myQuery, double avgDl, int TotalNumOfDocs, boolean useSemantic) {
        initRank();
        double maxBM25 = 0;
        int nonSemanticTermsCount = 0;

        double sqrtOfSizeOfQuery = 1;
        for(SubQuery subQuery : myQuery.getSubQueries()) {
            //List<DocumentAndTermDataForRanking> data = myQuery.getSubQueries().get(0).getData();
            sqrtOfSizeOfQuery = Math.sqrt(subQuery.getTerms().size());
            for(Term term : subQuery.getTerms()){
                List<DocumentAndTermDataForRanking> data = subQuery.getData().get(term.getValue());
                if(data == null)
                    continue;
                if(!term.isSemanticTerm())
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

                    double tfIdf = doc.getTermInDocumentData().getNormalizedTermFreq() * IDF * 1;

                    DocRank docScore = new DocRank(doc.getDocumentData());
                    docScore.currBM25 = bm25;
                    docScore.addToPositions(currPos);
                    docScore.setDocLength(docSize);
                    docScore.setCosSimTop(tfIdf);

                    if(!term.isSemanticTerm())
                        docScore.addNonSemanticTerm();

                    DocRank search = allRankedDocs.get(doc.getDocumentData());
                    if (search == null) {
                        allRankedDocs.put(doc.getDocumentData(), docScore);
                    } else {
                        //addScoreToExistsScore(docScore, currPos);
                        search.addToBM25(docScore.getCurrBM25());
                        search.addToPositions(currPos);
                        search.addAmountToNonSemanticTermCount(docScore.amountOfNonSemanticTerms);
                        search.addToCosSim(docScore.getCosSimTop());
                    }

                }
            }
        }

        //PriorityQueue<DocRank> sortedByRank = new PriorityQueue<>(new toSort());


        for(DocRank docRank : allRankedDocs.values()){
            docRank.normalizeAmountOfNonSemanticTerms(nonSemanticTermsCount);
            docRank.normalizeCosSim(sqrtOfSizeOfQuery);
            if(docRank.currBM25 > maxBM25)
                maxBM25 = docRank.currBM25;
        }
        for(DocRank docRank : allRankedDocs.values()){
            docRank.normalizeBM25(maxBM25);
            docRank.calculatePositions();
            docRank.calculateScore(useSemantic);

            //start sorting...
            //sortedByRank.add(docRank);
        }
        List<DocRank> sortedByRank = new LinkedList<>(allRankedDocs.values());

        Collections.sort(sortedByRank, new toSort());
        //int count = Math.min(50, sortedByRank.size());
        int count = 0;
        //int count = allRankedDocs.size();
        HashSet<String> seenDocs = new HashSet<>();
        for(DocRank best : sortedByRank) {
            if(count >= 50)
                break;
            if(seenDocs.contains(best.getDocumentsDictionaryEntrance().getDocNo().replace(" ", "")))
                continue;
            seenDocs.add(best.getDocumentsDictionaryEntrance().getDocNo().replace(" ", ""));
            sortedToReturn.add(best.getDocumentsDictionaryEntrance());
            count++;
        }

        myQuery.setRetrievedDocuments(sortedToReturn);
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
        private double cosSimTop;

        public DocRank(DocumentsDictionaryEntrance documentsDictionaryEntrance) {
            this.documentsDictionaryEntrance = documentsDictionaryEntrance;
            this.score = 0;
            currBM25 = 0;
            posScore = 0;
            amountOfNonSemanticTerms = 0;
            positions = new ArrayList<>();
            cosSimTop = 0;
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

        public void calculateScore(boolean useSemantics){
            //System.out.println("bm25 = " + currBM25 + ", pos score = " + posScore);
            if(!useSemantics) {
                amountOfNonSemanticTerms = 0; //so the semantics will not ruin our results
            }

            score = currBM25 * 0.299997 + cosSimTop * 0.099999 + amountOfNonSemanticTerms * 0.599994 + posScore * 0.00001;
        }

        public void addToPositions(List<Integer> pos){
            positions.add(pos);
        }

        public double getCosSimTop() {
            return cosSimTop;
        }

        public void addToCosSim(double cosSimTop) {
            this.cosSimTop += cosSimTop;
        }

        public void setCosSimTop(double cosSimTop) {
            this.cosSimTop = cosSimTop;
        }

        public void normalizeCosSim(double sqrtOfSizeOfQuery){
            cosSimTop /= (documentsDictionaryEntrance.getSumOfLeftSideOfCosSim() * sqrtOfSizeOfQuery);
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
            if(amountOfNonSemanticTerms == 0)
                this.amountOfNonSemanticTerms = 0;
            else
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
            /*
            double sum = 0;
            for(int i = 0; i < positions.size(); i++) {
                int min = Integer.MAX_VALUE;
                for (int posI : positions.get(i)) {
                    int curr = posI;
                    if (curr < min)
                        min = curr;
                }
                if(positions.get(i).size() == 0)
                    min = 0;

                sum += min / docLength;

            }
            sum /= positions.size();
            posScore = sum;
            */

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
