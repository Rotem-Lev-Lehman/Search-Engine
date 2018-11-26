package Model;

public interface IPosting {
    /** insert a new posting row to the posting
     * @param postingRow - the posting row that we wanted to save
     * @return a pointer to the new posting row created
     */
    int createNewPostingRow(APostingRow postingRow);
    APostingRow getPostingRow(int ptr);
    void updatePostingRow(int ptr, APostingRow newPostingRow);
}
