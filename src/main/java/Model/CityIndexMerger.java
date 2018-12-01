package Model;

public class CityIndexMerger extends AIndexMerger {
    @Override
    protected AIndex CreateNewIndex() {
        return new CityIndex();
    }
}
