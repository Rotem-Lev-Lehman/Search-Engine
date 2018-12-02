package Model;

public class TupleEntranceRowAndTerm {
    private EntranceRow entranceRow;
    private Term term;

    public TupleEntranceRowAndTerm(EntranceRow entranceRow, Term term) {
        this.entranceRow = entranceRow;
        this.term = term;
    }

    public EntranceRow getEntranceRow() {
        return entranceRow;
    }

    public void setEntranceRow(EntranceRow entranceRow) {
        this.entranceRow = entranceRow;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
}
