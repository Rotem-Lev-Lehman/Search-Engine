package Model;

import java.io.Serializable;

public class CityDictionaryEntrance extends ADictionaryEntrance implements Serializable {
    private String currency;
    private String populationSize;
    private String countryName;

    /**
     * @param cityName
     * @param countryName
     * @param currency
     * @param populationSize
     * @param docFreq
     * @param totalTermFreq
     * @param postingPtr
     * Constructor for CityDictionaryEntrance
     */
    public CityDictionaryEntrance(String cityName, String countryName, String currency, String populationSize, int docFreq, int totalTermFreq, int postingPtr) {
        super(cityName, docFreq, totalTermFreq, postingPtr);
        this.currency = currency;
        this.populationSize = populationSize;
        this.countryName = countryName;
    }

    /**
     * @return Getter for the Currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter for the Term
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return Getter for the populationSize
     */
    public String getPopulationSize() {
        return populationSize;
    }

    /**
     * Setter for the populationSize
     */
    public void setPopulationSize(String populationSize) {
        this.populationSize = populationSize;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public static ADictionaryEntrance ParseDictionaryRowAsMyKind(String dictionaryRow) {
        //String cityName, String countryName, String currency, String populationSize, int docFreq, int totalTermFreq\n don't need to save:"int postingPtr"
        String[] split = dictionaryRow.split(";");
        return new CityDictionaryEntrance(split[0], split[1], split[2], split[3], Integer.parseInt(split[4]), Integer.parseInt(split[5]),0);
    }

    @Override
    public String toString() {
        //String cityName, String countryName, String currency, String populationSize, int docFreq, int totalTermFreq\n don't need to save:"int postingPtr"
        return getTerm() + ';' + countryName + ';' + currency + ';' + populationSize + ';' + getDocFreq() + ';' + getTotalTermFreq() + '\n';
    }
}
