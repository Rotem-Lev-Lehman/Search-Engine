package Model;

import java.util.ArrayList;
//Posting
public class Posting {
    private ArrayList<APostingRow> PostingList;
    private APostingRow postingRow;
    private volatile Object lock;
    public Posting(){
        this.PostingList = new ArrayList<APostingRow>();
        lock = new Object();
    }

    /** insert a new posting row to the posting
     * @param postingRow - the posting row that we wanted to save
     * @return a pointer to the new posting row created
     */
    public int createNewPostingRow(APostingRow postingRow){
        int ptr;
        synchronized (lock) {
            PostingList.add(postingRow);
            ptr = PostingList.size() - 1;
        }
        return ptr;
    }
    public APostingRow getPostingRow(int ptr){
        APostingRow postingRow;
        synchronized (lock) {
             postingRow =PostingList.get(ptr);
        }
        return postingRow;
    }
    public void updatePostingRow(int ptr, APostingRow newPostingRow){
        synchronized (lock) {
            PostingList.add(ptr,newPostingRow);
        }
    }
}
