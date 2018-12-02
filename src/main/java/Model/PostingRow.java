package Model;

import java.io.Serializable;
import java.util.ArrayList;
//APostingRow
public class PostingRow implements Serializable {
    private ArrayList<EntranceRow> entranceRows;


    /**
     * @param entranceRows
     * constructor for PostingRow
     */
    public PostingRow(ArrayList<EntranceRow> entranceRows){
        this.entranceRows = entranceRows;
    }

    /**
     * @return getter for the entranceRows
     */
    public ArrayList<EntranceRow> getEntranceRows() {
        return entranceRows;
    }

    /**
     * setter for the entranceRows
     */
    public void setEntranceRows(ArrayList<EntranceRow> entranceRows) {
        this.entranceRows = entranceRows;
    }

    /**
     * merge the entranceRows of two posting rows
     */
    public void merge(PostingRow other){
        this.entranceRows.addAll(other.entranceRows);
    }
}
