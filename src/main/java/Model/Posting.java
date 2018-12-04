package Model;

import java.util.ArrayList;
//Posting
public class Posting {
    private ArrayList<PostingRow> PostingList;
    private volatile Object lock;


    /**
     * Constructor for the posting!
     */
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

    /**
     * @param ptr
     * @return Getter for a postingRow
     */
    public PostingRow getPostingRow(int ptr){
        PostingRow postingRow;
        synchronized (lock) {
             postingRow = PostingList.get(ptr);
        }
        return postingRow;
    }

    public void setPostingList(ArrayList<PostingRow> postingList){
        this.PostingList = postingList;
    }

    /**
     * @param ptr
     * @param newPostingRow
     * update a postingRow
     */
    public void updatePostingRow(int ptr, PostingRow newPostingRow){
        synchronized (lock) {
            PostingList.add(ptr,newPostingRow);
        }
    }

    /**
     * @return Getter for PostingList ( all Posting Rows in the posting )
     */
    public ArrayList<PostingRow> getPostingList(){
        return PostingList;
    }

    /**
     * @return true if PostingList is not empty or false if PostingList is empty
     */
    public boolean isEmpty(){
        boolean ans;
        synchronized (lock){
            ans = PostingList.isEmpty();
        }
        return ans;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (PostingRow row : PostingList) {
            builder.append(row.toString());
        }
        return builder.toString();
    }
}
