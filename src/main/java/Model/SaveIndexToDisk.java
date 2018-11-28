package Model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveIndexToDisk {
    private static volatile int numOfAvailableIndex = 0;
    private static volatile Object lock = new Object();

    public void save(AIndex index){
        int currentNumOfIndex;

        synchronized (lock){
            currentNumOfIndex = numOfAvailableIndex;
            numOfAvailableIndex++;
        }

        String dictionaryFileName = "C:\\Users\\User\\Desktop\\אחזור מידע\\idices\\" + "dic" + currentNumOfIndex + ".data";
        String postingFileName = "C:\\Users\\User\\Desktop\\אחזור מידע\\idices\\" + "post" + currentNumOfIndex + ".data";

        try {
            ObjectOutputStream  dictionaryOutput = new ObjectOutputStream(new FileOutputStream(dictionaryFileName));
            dictionaryOutput.writeObject(index.getDictionary().getMap());
            dictionaryOutput.close();

            ObjectOutputStream  postingOutput = new ObjectOutputStream(new FileOutputStream(postingFileName));
            postingOutput.writeObject(index.getPosting().getPostingList());
            postingOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
