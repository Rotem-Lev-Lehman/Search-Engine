package Model;

public class TermsIndex extends AIndex {
    /**
     * @param term
     * @param df
     * @param ptr
     * @return Create a Dictionary Entrance
     */
    @Override
    protected ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr) {
        return new TermsDictionaryEntrance(term.getValue(), df, ptr);
    }
}
