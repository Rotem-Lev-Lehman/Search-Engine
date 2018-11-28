package Model;

import java.util.List;

public class CityIndexMerger extends AIndexMerger {
    public CityIndexMerger(AIndex[] indices){
        super(indices);
        this.merged = new CityIndex();
    }
}
