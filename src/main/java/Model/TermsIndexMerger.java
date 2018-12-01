package Model;

public class TermsIndexMerger extends AIndexMerger {
    @Override
    protected AIndex CreateNewIndex() {
        return new TermsIndex();
    }
}
