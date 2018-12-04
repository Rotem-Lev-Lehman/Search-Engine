package Model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

public class SaveIndexToDisk {
    private static volatile int numOfSmallLettersIndex = 0;
    private static volatile int numOfBigLettersIndex = 0;
    private static volatile int numOfCityIndex = 0;
    private static volatile Object smallLettersLock = new Object();
    private static volatile Object bigLettersLock = new Object();
    private static volatile Object cityLock = new Object();

    private String folder;

    public SaveIndexToDisk(){
        this.folder = "C:\\Users\\User\\Desktop\\אחזור מידע\\indices\\";
    }

    public void save(AIndex index){
        int currentNumOfIndex;

        String subFolder;

        if(index.getType() == TypeOfIndex.SmallLetters) {
            subFolder = "smallLetters\\";
            synchronized (smallLettersLock){
                currentNumOfIndex = numOfSmallLettersIndex;
                numOfSmallLettersIndex++;
            }
        }
        else if(index.getType() == TypeOfIndex.BigLetters) {
            subFolder = "bigLetters\\";
            synchronized (bigLettersLock){
                currentNumOfIndex = numOfBigLettersIndex;
                numOfBigLettersIndex++;
            }
        }
        else { // City
            subFolder = "cities\\";
            synchronized (cityLock){
                currentNumOfIndex = numOfCityIndex;
                numOfCityIndex++;
            }
        }

        String totalFolder = folder + subFolder;

        String dictionaryFileName = totalFolder + "dic" + currentNumOfIndex + ".data";
        String postingFileName = totalFolder + "post" + currentNumOfIndex + ".data";

        index.SortAll();
        try {
            OutputStreamWriter dictionaryOutput = new OutputStreamWriter(new FileOutputStream(dictionaryFileName));
            dictionaryOutput.write(index.getDictionary().toString());
            dictionaryOutput.close();

            OutputStreamWriter postingOutput = new OutputStreamWriter(new FileOutputStream(postingFileName));
            postingOutput.write(index.getPosting().toString());
            postingOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
