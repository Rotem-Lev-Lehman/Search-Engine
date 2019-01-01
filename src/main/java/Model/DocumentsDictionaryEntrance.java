package Model;

import java.util.Objects;

public class DocumentsDictionaryEntrance {
    private String DocNo;
    private String FileName;
    private int uniqueWordsAmount;
    private int maxTf;
    private String city;
    private IdentityAndScore[] topFiveBigWords;
    private double sumOfLeftSideOfCosSim;
    public Object lockSumOfLeftSide = new Object();

    public DocumentsDictionaryEntrance(String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city) {
        DocNo = docNo;
        FileName = fileName;
        this.uniqueWordsAmount = uniqueWordsAmount;
        this.maxTf = maxTf;
        this.city = city;
        topFiveBigWords = new IdentityAndScore[5];
        for (int i = 0; i < topFiveBigWords.length; i++)
            topFiveBigWords[i] = new IdentityAndScore(null, 0);
        sumOfLeftSideOfCosSim = 0;
    }

    public DocumentsDictionaryEntrance(String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city, IdentityAndScore[] topFiveBigWords, double sumOfLeftSideOfCosSim){
        DocNo = docNo;
        FileName = fileName;
        this.uniqueWordsAmount = uniqueWordsAmount;
        this.maxTf = maxTf;
        this.city = city;
        this.topFiveBigWords = topFiveBigWords;
        this.sumOfLeftSideOfCosSim = sumOfLeftSideOfCosSim;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public int getUniqueWordsAmount() {
        return uniqueWordsAmount;
    }

    public void setUniqueWordsAmount(int uniqueWordsAmount) {
        this.uniqueWordsAmount = uniqueWordsAmount;
    }

    public int getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public IdentityAndScore[] getTopFiveBigWords(){
        return topFiveBigWords;
    }

    public void AddToSumOfCossim(double num){
        sumOfLeftSideOfCosSim += Math.pow(num,2);
    }

    public double getSumOfLeftSideOfCosSim() {
        return sumOfLeftSideOfCosSim;
    }

    public void setSumOfLeftSideOfCosSim(double sumOfLeftSideOfCosSim) {
        this.sumOfLeftSideOfCosSim = sumOfLeftSideOfCosSim;
    }

    public static DocumentsDictionaryEntrance Parse(String entrance, boolean usePreviousData) {
        //String docNo, String fileName, int uniqueWordsAmount, int maxTf, double cossimSum (round by *10000), String city, (IdAndScore) * 5
        String[] split = entrance.split(";");
        String docNo = split[0];
        String fileName = split[1];
        int uniqueWordsAmount = Integer.parseInt(split[2]);
        int maxTf = Integer.parseInt(split[3]);
        int rounded = Integer.parseInt(split[4]);
        double cossimSumWithSqrt = (double) rounded / 10000.0;
        String city = null;
        if (!split[5].equals("#"))
            city = split[5];

        IdentityAndScore[] topFive = new IdentityAndScore[5];
        for (int i = 0; i < topFive.length; i++) {
            if(usePreviousData)
                topFive[i] = IdentityAndScore.Parse(split[6 + i]);
            else
                topFive[i] = new IdentityAndScore(null,0);
        }

        return new DocumentsDictionaryEntrance(docNo, fileName, uniqueWordsAmount, maxTf, city, topFive,cossimSumWithSqrt);
    }

    @Override
    public String toString() {
        //String docNo, String fileName, int uniqueWordsAmount, int maxTf, double cossimSum (round by *10000), String city, (String term, int score) * 5
        StringBuilder builder = new StringBuilder();
        builder.append(DocNo).append(";").append(FileName).append(";").append(uniqueWordsAmount).append(";").append(maxTf).append(";");
        double sqrt = Math.sqrt(sumOfLeftSideOfCosSim);
        int round = (int)Math.round(sqrt * 10000);
        builder.append(round).append(";");

        if(city != null)
            builder.append(city);
        else
            builder.append("#");
        builder.append(";");
        for(int i = 0; i < topFiveBigWords.length; i++)
        {
            builder.append(topFiveBigWords[i].toString());

            if(i < topFiveBigWords.length - 1)
                builder.append(";");
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentsDictionaryEntrance entrance = (DocumentsDictionaryEntrance) o;
        return DocNo.equals(entrance.DocNo) &&
                FileName.equals(entrance.FileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DocNo, FileName);
    }
}
