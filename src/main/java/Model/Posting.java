package Model;

import java.util.ArrayList;
//Posting
public class Posting {
    private ArrayList<PostingRow> PostingList;
    private volatile Object lock;

    public Posting(){
        this.PostingList = new ArrayList<PostingRow>();
        lock = new Object();
    }

    /** insert a new posting row to the posting
     * @param postingRow - the posting row that we wanted to save
     * @return a pointer to the new posting row created
     */
    public int createNewPostingRow(PostingRow postingRow){
        int ptr;
        synchronized (lock) {
            PostingList.add(postingRow);
            ptr = PostingList.size() - 1;
        }
        return ptr;
    }
    public PostingRow getPostingRow(int ptr){
        PostingRow postingRow;
        synchronized (lock) {
             postingRow = PostingList.get(ptr);
        }
        return postingRow;
    }
    public void updatePostingRow(int ptr, PostingRow newPostingRow){
        synchronized (lock) {
            PostingList.add(ptr,newPostingRow);
        }
    }
}
