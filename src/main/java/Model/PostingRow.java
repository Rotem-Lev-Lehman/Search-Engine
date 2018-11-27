package Model;

import java.util.ArrayList;
//APostingRow
public class PostingRow {
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
}
