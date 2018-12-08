package Model;

public class DocumentsDictionaryEntrance {
    private String DocNo;
    private String FileName;
    private int uniqueWordsAmount;
    private int maxTf;
    private String city;

    public DocumentsDictionaryEntrance(String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city) {
        DocNo = docNo;
        FileName = fileName;
        this.uniqueWordsAmount = uniqueWordsAmount;
        this.maxTf = maxTf;
        this.city = city;
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

    @Override
    public String toString() {
        //String docNo, String fileName, int uniqueWordsAmount, int maxTf, String city
        StringBuilder builder = new StringBuilder();
        builder.append(DocNo).append(";").append(FileName).append(";").append(uniqueWordsAmount).append(";").append(maxTf).append(";");
        if(city != null)
            builder.append(city);
        else
            builder.append("#");
        builder.append("\n");
        return builder.toString();
    }
}
