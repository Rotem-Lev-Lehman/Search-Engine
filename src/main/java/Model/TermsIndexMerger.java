package Model;

import java.util.List;

public class TermsIndexMerger extends AIndexMerger {
    public TermsIndexMerger(AIndex[] indices){
        super(indices);
        this.merged = new TermsIndex();
    }
}
