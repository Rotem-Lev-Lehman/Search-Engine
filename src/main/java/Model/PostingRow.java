package Model;

import java.io.Serializable;
import java.util.ArrayList;
//APostingRow
public class PostingRow implements Serializable {
    private ArrayList<EntranceRow> entranceRows;



    public PostingRow(ArrayList<EntranceRow> entranceRows){
        this.entranceRows = entranceRows;
    }

    public ArrayList<EntranceRow> getEntranceRows() {
        return entranceRows;
    }

    public void setEntranceRows(ArrayList<EntranceRow> entranceRows) {
        this.entranceRows = entranceRows;
    }

    public void merge(PostingRow other){
        this.entranceRows.addAll(other.entranceRows);
    }
}
