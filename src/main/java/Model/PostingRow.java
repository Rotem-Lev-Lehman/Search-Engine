package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static PostingRow ParsePostingRow(String postingRow){
        //docId(int);tf(int - *1000 from what it is...);[int;int;int;...;int](indexList)&...
        //first split by -
        String[] differentEntrances = postingRow.split("&");
        ArrayList<EntranceRow> entranceRows = new ArrayList<EntranceRow>();
        for(int i = 0; i < differentEntrances.length; i++) {
            String[] firstSplit = differentEntrances[i].split("\\[");
            String[] firstStuff = firstSplit[0].split(";");
            String[] secondStuff = firstSplit[1].split("]")[0].split(";");
            //String[] firstStuff = differentEntrances[i].split(";");
            List<Integer> places = new ArrayList<>();
            for (String num : secondStuff) {
                places.add(Integer.parseInt(num));
            }
            double tf = Double.parseDouble(firstStuff[1])/1000.0;
            EntranceRow entrance = new EntranceRow(1,places);
            //EntranceRow entrance = new EntranceRow(1);
            entrance.setNormalizedTermFreq(tf);
            entrance.setDocId(Integer.parseInt(firstStuff[0]));
            entranceRows.add(entrance);
        }
        return new PostingRow(entranceRows);
    }

    @Override
    public String toString(){
        //docId(int);tf(int - *1000 from what it is...);[int;int;int;...;int](indexList)&...
        StringBuilder builder = new StringBuilder();
        //Collections.sort(entranceRows);
        for (int j = 0; j < entranceRows.size(); j++) {
            EntranceRow entrance = entranceRows.get(j);
            builder.append(entrance.getDocId()).append(";").append(Math.round(entrance.getNormalizedTermFreq() * 1000)).append(";");
            builder.append('[');
            List<Integer> pos = entrance.getPositions();
            for(int i = 0; i < pos.size(); i++){
                builder.append(pos.get(i));
                if(i < pos.size() - 1)
                    builder.append(";");
            }
            builder.append(']');
            if(j < entranceRows.size() - 1)
                builder.append('&');
        }
        builder.append('\n');
        return builder.toString();
    }
}
