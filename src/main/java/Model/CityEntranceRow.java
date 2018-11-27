package Model;
//Expand to a city Entrance
public class CityEntranceRow extends AEntranceRow {
    private String countryName;
    private String kindOfCoin;
    private int sizeOfPopulation;

    public CityEntranceRow(String docNo, String fileName, int termFreqInDoc, String countryName, String kindOfCoin, int sizeOfPopulation) {
        super(docNo, fileName, termFreqInDoc);
        this.countryName = countryName;
        this.kindOfCoin = kindOfCoin;
        this.sizeOfPopulation = sizeOfPopulation;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getKindOfCoin() {
        return kindOfCoin;
    }

    public void setKindOfCoin(String kindOfCoin) {
        this.kindOfCoin = kindOfCoin;
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void setSizeOfPopulation(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
    }


}

