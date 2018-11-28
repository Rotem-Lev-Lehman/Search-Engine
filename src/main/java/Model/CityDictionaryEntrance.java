package Model;

import java.io.Serializable;

public class CityDictionaryEntrance extends ADictionaryEntrance implements Serializable {
    private String currency;
    private String populationSize;

    public CityDictionaryEntrance(String cityName, String currency, String populationSize, int docFreq, int postingPtr) {
        super(cityName, docFreq, postingPtr);
        this.currency = currency;
        this.populationSize = populationSize;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(String populationSize) {
        this.populationSize = populationSize;
    }
}
