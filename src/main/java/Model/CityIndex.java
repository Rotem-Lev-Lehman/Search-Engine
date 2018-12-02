package Model;

public class CityIndex extends AIndex {
    @Override
    /**
     *
     */
    protected ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr) {
        //get currency and populationSize from the API of cities.
        String currency = null;
        String populationSize = null;

        return new CityDictionaryEntrance(term.getValue(),currency,populationSize,df,ptr);
    }
}
