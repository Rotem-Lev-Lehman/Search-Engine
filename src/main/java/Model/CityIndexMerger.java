package Model;

public class CityIndexMerger extends AIndexMerger {
    /**
     * Constructor for the Indexer of the merge of City Indexers.
     */
    @Override
    protected AIndex CreateNewIndex() {
        return new CityIndex();
    }
}
