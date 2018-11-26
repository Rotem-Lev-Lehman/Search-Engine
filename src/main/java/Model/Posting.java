package Model;

public class Posting {
    /** insert a new posting row to the posting
     * @param postingRow - the posting row that we wanted to save
     * @return a pointer to the new posting row created
     */
    public int createNewPostingRow(APostingRow postingRow){}
    public APostingRow getPostingRow(int ptr){}
    public void updatePostingRow(int ptr, APostingRow newPostingRow){}
}
