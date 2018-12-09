package Model;

import java.io.*;

public class SaveIndexToDisk {
    private static volatile int[] numOfSmallLettersIndex = new int[26];
    private static volatile int[] numOfBigLettersIndex = new int[26];
    private static volatile int numOfCityIndex = 0;
    private static volatile int numOfNumIndex = 0;
    private static volatile int numOfRangeOrPhraseIndex = 0;
    private static volatile int numOfPercentageIndex = 0;
    private static volatile int numOfPriceIndex = 0;
    private static volatile int numOfDateIndex = 0;

    private static volatile Object[] smallLettersLock = new Object[26];
    private static volatile Object[] bigLettersLock = new Object[26];
    private static volatile Object cityLock = new Object();
    private static volatile Object numLock = new Object();
    private static volatile Object rangeOrPhraseLock = new Object();
    private static volatile Object percentageLock = new Object();
    private static volatile Object priceLock = new Object();
    private static volatile Object dateLock = new Object();

    private static String folder = null;

    public static void initialize(){
        for(int i =0; i < numOfSmallLettersIndex.length; i++){
            numOfSmallLettersIndex[i] = 0;
            numOfBigLettersIndex[i] = 0;
            smallLettersLock[i] = new Object();
            bigLettersLock[i] = new Object();
        }
    }

    public static void setFolder(String foldername){
        folder = foldername + '\\';

        String smallFolder = folder + "smallLetters";
        String bigFolder = folder + "bigLetters";
        String cityFolder = folder + "cities";
        String numFolder = folder + "numbers";
        String rangeOrPhraseFolder = folder + "rangeOrPhrase";
        String percentageFolder = folder + "percentage";
        String priceFolder = folder + "price";
        String dateFolder = folder + "date";

        File directory = new File(foldername);

        File small = new File(smallFolder);
        File big = new File(bigFolder);

        File[] smallLetters = new File[26];
        File[] bigLetters = new File[26];
        for(int i = 0; i < smallLetters.length; i++){
            char currLetter = (char)('a' + i);
            smallLetters[i] = new File(smallFolder + "\\" + currLetter);
            bigLetters[i] = new File(bigFolder + "\\" + currLetter);
        }

        File city = new File(cityFolder);
        File num = new File(numFolder);
        File rangeOrPhrase = new File(rangeOrPhraseFolder);
        File percentage = new File(percentageFolder);
        File price = new File(priceFolder);
        File date = new File(dateFolder);

        if (!directory.exists()){
            directory.mkdirs();
        }
        if(!small.exists()){
            small.mkdir();
        }
        if(!big.exists()){
            big.mkdir();
        }

        for(int i = 0; i < smallLetters.length; i++){
            if(!smallLetters[i].exists())
                smallLetters[i].mkdir();
            if(!bigLetters[i].exists())
                bigLetters[i].mkdir();
        }

        if(!city.exists()){
            city.mkdir();
        }
        if(!num.exists()){
            num.mkdir();
        }
        if(!rangeOrPhrase.exists()){
            rangeOrPhrase.mkdir();
        }
        if(!percentage.exists()){
            percentage.mkdir();
        }
        if(!price.exists()){
            price.mkdir();
        }
        if(!date.exists()){
            date.mkdir();
        }
    }

    public void save(AIndex index){
        if(index.isEmpty())
            return;
        int currentNumOfIndex;

        String subFolder;

        if(index.getType() == TypeOfTerm.SmallLetters) {
            int i = index.getNumOfLetter();
            synchronized (smallLettersLock[i]) {
                currentNumOfIndex = numOfSmallLettersIndex[i];
                numOfSmallLettersIndex[i]++;
            }
            char currLetter = (char)('a' + i);
            subFolder = "smallLetters\\"  + currLetter + "\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.BigLetters) {
            int i = index.getNumOfLetter();
            synchronized (bigLettersLock[i]) {
                currentNumOfIndex = numOfBigLettersIndex[i];
                numOfBigLettersIndex[i]++;
            }
            char currLetter = (char)('a' + i);
            subFolder = "bigLetters\\"  + currLetter + "\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.City) {
            synchronized (cityLock){
                currentNumOfIndex = numOfCityIndex;
                numOfCityIndex++;
            }
            subFolder = "cities\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.Number) {
            synchronized (numLock){
                currentNumOfIndex = numOfNumIndex;
                numOfNumIndex++;
            }
            subFolder = "numbers\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.RangeOrPhrase) {
            synchronized (rangeOrPhraseLock){
                currentNumOfIndex = numOfRangeOrPhraseIndex;
                numOfRangeOrPhraseIndex++;
            }
            subFolder = "rangeOrPhrase\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.Percentage) {
            synchronized (percentageLock){
                currentNumOfIndex = numOfPercentageIndex;
                numOfPercentageIndex++;
            }
            subFolder = "percentage\\" + currentNumOfIndex + "\\";
        }
        else if(index.getType() == TypeOfTerm.Price) {
            synchronized (priceLock){
                currentNumOfIndex = numOfPriceIndex;
                numOfPriceIndex++;
            }
            subFolder = "price\\" + currentNumOfIndex + "\\";
        }
        else { //date
            synchronized (dateLock){
                currentNumOfIndex = numOfDateIndex;
                numOfDateIndex++;
            }
            subFolder = "date\\" + currentNumOfIndex + "\\";
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
            dictionaryOutput.flush();
            dictionaryOutput.close();

            OutputStreamWriter postingOutput = new OutputStreamWriter(new FileOutputStream(postingFileName));
            postingOutput.write(index.getPosting().toString());
            postingOutput.flush();
            postingOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
