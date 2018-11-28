package Model;

import java.util.*;

public abstract class AIndexMerger {
    protected AIndex merged;
    private AIndex[] indices;

    public AIndexMerger(AIndex[] indices){
         this.indices = indices;
    }

    public AIndex Merge(){
        for(int i = 0; i<indices.length;i++) {
            for (Map.Entry<String, ADictionaryEntrance> pair : indices[i].getDictionary().getMap().entrySet()){
                PostingRow row = indices[i].getPosting().getPostingRow(pair.getValue().getPostingPtr());

                ADictionaryEntrance mergedDictionaryEntrance = merged.getDictionary().getMap().get(pair.getKey());
                if(mergedDictionaryEntrance != null){
                    //exist
                    mergedDictionaryEntrance.addToDocFreq(pair.getValue().getDocFreq());

                    PostingRow mergedPostingRow = merged.getPosting().getPostingRow(mergedDictionaryEntrance.getPostingPtr());
                    mergedPostingRow.merge(row);
                }
                else{
                    //not exist
                    int ptr = merged.getPosting().createNewPostingRow(row);
                    pair.getValue().setPostingPtr(ptr);
                    merged.getDictionary().addEntrance(pair.getValue());
                }
            }
        }
        for (PostingRow row : merged.getPosting().getPostingList()){
            Collections.sort(row.getEntranceRows()); // sort everything before saving to disk
        }
        return merged;
    }
}
