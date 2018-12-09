package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public abstract class AIndexFileController {
    private File dictionaryFile;
    private File postingFile;
    private Scanner dictionaryScanner;
    private Scanner postingScanner;
    protected PostingRow currentPostingRow;
    protected ADictionaryEntrance currentDictionaryEntrance;
    private boolean done;

    public void OpenFile(String folderName){
        String dictionaryFilename = folderName + '\\' + "dic.data";
        String postingFilename = folderName + '\\' + "post.data";
        dictionaryFile = new File(dictionaryFilename);
        postingFile = new File(postingFilename);
        done = false;
        try {
            dictionaryScanner = new Scanner(new BufferedReader(new FileReader(dictionaryFile)));
            postingScanner = new Scanner(new BufferedReader(new FileReader(postingFile)));
        } catch (FileNotFoundException e) {
            done = true;
            //e.printStackTrace();
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
                postingScanner.close();
                dictionaryScanner.close();
                return;
            }
            parseDictionaryRow(dictionaryRow);
            currentPostingRow = PostingRow.ParsePostingRow(postingRow);
        }
    }

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
