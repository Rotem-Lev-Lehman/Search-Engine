package Model;

public class IdentityAndScore {
    private String term;
    private double score;

    public IdentityAndScore(String term, double score){
        this.term = term;
        this.score = score;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public static IdentityAndScore Parse(String str){
        String[] split = str.split("&");
        String term = null;
        if(!split[0].equals("#"))
            term = split[0];
        double score = Double.parseDouble(split[1]) / 100000.0;
        return new IdentityAndScore(term, score);
    }

    @Override
    public String toString() {
        String curr;
        if(term != null)
            curr = term;
        else
            curr = "#";

        curr += "&";
        //round the score to 5 decimal numbers (save space in the disk, but still keep the value of the number)
        int rounded = (int)Math.round(score * 100000);
        curr += rounded;
        return curr;
    }
}
