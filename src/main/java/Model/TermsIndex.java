package Model;

public class TermsIndex extends AIndex {
    /**
     * @param term
     * @param df
     * @param ptr
     * @return Create a Dictionary Entrance
     */
    @Override
    protected ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr, CityInfo info, int tf) {
        return new TermsDictionaryEntrance(term.getValue(), df, tf, ptr);
    }
}
