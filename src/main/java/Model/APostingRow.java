package Model;

import java.util.ArrayList;

public abstract class APostingRow {
    private ArrayList<AEntranceRow> entranceRows;



    public APostingRow(ArrayList<AEntranceRow> entranceRows){
        this.entranceRows = entranceRows;
    }

    public ArrayList<AEntranceRow> getEntranceRows() {
        return entranceRows;
    }

    public void setEntranceRows(ArrayList<AEntranceRow> entranceRows) {
        this.entranceRows = entranceRows;
    }
}
