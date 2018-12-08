package Model;

public class CityIndex extends AIndex {
    @Override
    /**
     * constructor for the CityIndex - make new CityDictionaryEntrance.
     */
    protected ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr, CityInfo info, int tf) {
        String currency = info.getCurrency();
        String populationSize = info.getPopulationSize();
        String country = info.getCountry();

        return new CityDictionaryEntrance(term.getValue(), country,currency,populationSize,df, tf,ptr);
    }
}
