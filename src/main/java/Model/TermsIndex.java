package Model;

public class TermsIndex extends AIndex {
    @Override
    protected ADictionaryEntrance getRightDictionaryEntrance(Term term, int df, int ptr) {
        return new TermsDictionaryEntrance(term.getValue(), df, ptr);
    }
}
