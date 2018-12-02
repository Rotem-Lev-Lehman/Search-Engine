package Model;

public class TermsIndexMerger extends AIndexMerger {
    /**
     * @return Create New Term Index
     */
    @Override
    protected AIndex CreateNewIndex() {
        return new TermsIndex();
    }
}
