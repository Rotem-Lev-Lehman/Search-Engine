package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class AIndexFileController {
    private File dictionaryFile;
    private File postingFile;
    private Scanner dictionaryScanner;
    private Scanner postingScanner;
    private PostingRow currentPostingRow;
    private ADictionaryEntrance currentDictionaryEntrance;
    private boolean done;

    public void OpenFile(String dictionaryFilename, String postingFilename){
        dictionaryFile = new File(dictionaryFilename);
        postingFile = new File(postingFilename);
        done = false;
        try {
            dictionaryScanner = new Scanner(dictionaryFile);
            postingScanner = new Scanner(postingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getNextRow(){
        if(!done) {
            String dictionaryRow = null;
            String postingRow = null;
            if (dictionaryScanner.hasNext())
                dictionaryRow = dictionaryScanner.nextLine();
            if (postingScanner.hasNext())
                postingRow = postingScanner.nextLine();
            if (dictionaryRow == null || postingRow == null) { // supposed to be together...
                done = true;
                return;
            }
            parseDictionaryRow(dictionaryRow);
            parsePostingRow(postingRow);
        }
    }

    protected abstract void parsePostingRow(String postingRow);

    protected abstract void parseDictionaryRow(String dictionaryRow);

    public boolean done(){
        return done;
    }

    public String getTerm(){
        return currentDictionaryEntrance.getTerm();
    }

    public PostingRow getPostingRow(){
        return currentPostingRow;
    }

    public ADictionaryEntrance getDictionaryEntrance(){
        return currentDictionaryEntrance;
    }

    public int getDocFreq(){
        return currentDictionaryEntrance.getDocFreq();
    }
}
