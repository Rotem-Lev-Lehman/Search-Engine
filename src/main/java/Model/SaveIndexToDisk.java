package Model;

import java.io.*;

public class SaveIndexToDisk {
    private static volatile int numOfSmallLettersIndex = 0;
    private static volatile int numOfBigLettersIndex = 0;
    private static volatile int numOfCityIndex = 0;
    private static volatile Object smallLettersLock = new Object();
    private static volatile Object bigLettersLock = new Object();
    private static volatile Object cityLock = new Object();

    private static String folder = null;

    public static void setFolder(String foldername){
        folder = foldername + '\\';

        String smallFolder = folder + "smallLetters";
        String bigFolder = folder + "bigLetters";
        String cityFolder = folder + "cities";

        File directory = new File(foldername);
        File small = new File(smallFolder);
        File big = new File(bigFolder);
        File city = new File(cityFolder);

        if (!directory.exists()){
            directory.mkdirs();
        }
        if(!small.exists()){
            small.mkdir();
        }
        if(!big.exists()){
            big.mkdir();
        }
        if(!city.exists()){
            city.mkdir();
        }
    }

    public void save(AIndex index){
        int currentNumOfIndex;

        String subFolder;

        if(index.getType() == TypeOfIndex.SmallLetters) {
            synchronized (smallLettersLock){
                currentNumOfIndex = numOfSmallLettersIndex;
                numOfSmallLettersIndex++;
            }
            subFolder = "smallLetters\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfIndex.BigLetters) {
            synchronized (bigLettersLock){
                currentNumOfIndex = numOfBigLettersIndex;
                numOfBigLettersIndex++;
            }
            subFolder = "bigLetters\\" + currentNumOfIndex + "\\";
        }
        else { // City
            synchronized (cityLock){
                currentNumOfIndex = numOfCityIndex;
                numOfCityIndex++;
            }
            subFolder = "cities\\" + currentNumOfIndex + "\\";
        }

        String totalFolder = folder + subFolder;

        File directory = new File(totalFolder);
        if (!directory.exists()){
            directory.mkdir();
        }

        String dictionaryFileName = totalFolder + "dic.data";
        String postingFileName = totalFolder + "post.data";

        index.SortAll(); // sort the index's posting so it will fit the dictionary
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
