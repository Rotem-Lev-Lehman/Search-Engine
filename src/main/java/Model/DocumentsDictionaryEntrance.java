package Model;

public class DocumentsDictionaryEntrance {
    private String DocNo;
    private String FileName;
    private int uniqueWordsAmount;
    private int maxTf;
    private String city;
    private IdentityAndScore[] topFiveBigWords;

    public DocumentsDictionaryEntrance(String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city) {
        DocNo = docNo;
        FileName = fileName;
        this.uniqueWordsAmount = uniqueWordsAmount;
        this.maxTf = maxTf;
        this.city = city;
        topFiveBigWords = new IdentityAndScore[5];
        for (int i = 0; i < topFiveBigWords.length; i++)
            topFiveBigWords[i] = new IdentityAndScore(null, 0);
    }

    public DocumentsDictionaryEntrance(String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city, IdentityAndScore[] topFiveBigWords){
        DocNo = docNo;
        FileName = fileName;
        this.uniqueWordsAmount = uniqueWordsAmount;
        this.maxTf = maxTf;
        this.city = city;
        this.topFiveBigWords = topFiveBigWords;
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

    public static DocumentsDictionaryEntrance Parse(String entrance) {
        //String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city, (IdAndScore) * 5
        String[] split = entrance.split(";");
        String docNo = split[0];
        String fileName = split[1];
        int uniqueWordsAmount = Integer.parseInt(split[2]);
        int maxTf = Integer.parseInt(split[3]);
        String city = null;
        if (!split[4].equals("#"))
            city = split[4];

        IdentityAndScore[] topFive = new IdentityAndScore[5];
        for (int i = 0; i < topFive.length; i++) {
            topFive[i] = IdentityAndScore.Parse(split[5 + i]);
            //topFive[i] = new IdentityAndScore(null,0);
        }

        return new DocumentsDictionaryEntrance(docNo, fileName, uniqueWordsAmount, maxTf, city, topFive);
    }

    @Override
    public String toString() {
        //String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city, (String term, int score) * 5
        StringBuilder builder = new StringBuilder();
        builder.append(DocNo).append(";").append(FileName).append(";").append(uniqueWordsAmount).append(";").append(maxTf).append(";");
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
}
