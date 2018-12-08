package Model;

public class CityInfo {
    private String cityName;
    private String country;
    private String currency;
    private String populationSize;

    public CityInfo(String cityName,String country, String currency, String populationSize) {
        this.cityName = cityName;
        this.country = country;
        this.currency = currency;
        this.populationSize = populationSize;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
