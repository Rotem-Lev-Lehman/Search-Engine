package Model;

import java.io.Serializable;

public class CityDictionaryEntrance extends ADictionaryEntrance implements Serializable {
    private String currency;
    private String populationSize;

    /**
     * @param cityName
     * @param currency
     * @param populationSize
     * @param docFreq
     * @param postingPtr
     * Constructor for CityDictionaryEntrance
     */
    public CityDictionaryEntrance(String cityName, String currency, String populationSize, int docFreq, int postingPtr) {
        super(cityName, docFreq, postingPtr);
        this.currency = currency;
        this.populationSize = populationSize;
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

    public static ADictionaryEntrance ParseDictionaryRowAsMyKind(String dictionaryRow) {
        //String cityName; String currency; String populationSize; int docFreq\n don't need to save:"int postingPtr"
        String[] split = dictionaryRow.split(";");
        return new CityDictionaryEntrance(split[0], split[1], split[2],Integer.parseInt(split[3]),0);
    }

    @Override
    public String toString() {
        //String cityName, String currency, String populationSize, int docFreq, don't need to save:"int postingPtr"
        return getTerm() + ';' + currency + ';' + populationSize + ';' + getDocFreq() + '\n';
    }
}
