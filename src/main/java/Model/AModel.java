package Model;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public abstract class AModel {
    /**
     * The ReadFile class that can return a list of all the Documents in the path given
     */
    protected IReadFile readFile;

    protected volatile Queue<Document> documents;
    protected volatile boolean finishedRetrievingFiles;
    protected volatile Object lock = new Object();
    protected HashSet<String> stopWords;
    //protected AIndex smallLetterIndexer;
    //protected AIndex bigLetterIndexer;
    protected AIndex[] smallLetterIndexer; //one for each letter in the abc
    protected AIndex[] bigLetterIndexer; //one for each letter in the abc
    protected AIndex numbersIndexer;
    protected AIndex rangeOrPhraseIndexer;
    protected AIndex percentageIndexer;
    protected AIndex priceIndexer;
    protected AIndex dateIndexer;
    protected AIndex cityIndexer;
    protected DocumentsDictionary documentsDictionary;
    protected Semaphore empty;
    protected Semaphore full;
    protected Semaphore tasksLimit;
    protected String destPathForTotalIndices;
    protected String destPathForTempIndices;
    protected boolean stem;
    /** Creates all of the Documents in the given path
     * @param path - The path where all of the Documents are in
     */
    public void GetAllDocuments(String path){
        StopWatch stopWatch = new StopWatch();
        finishedRetrievingFiles = false;
        documents = new ArrayDeque<Document>();

        smallLetterIndexer = new AIndex[26];
        bigLetterIndexer = new AIndex[26];

        System.out.println("Starting to parse");
        stopWatch.start();

        startReadingFiles(path);
        startParsing();

        stopWatch.stop();
        System.out.println("Finished parsing");
        double time = stopWatch.getTime() / 60000.0;
        double seconds = time - (int)time;
        System.out.println("Total time = " + (int)time + " minutes and " + (int)(seconds*60) + " seconds");

        //done parsing and indexing all of the files.
        //now need to merge the indices:
        System.out.println();
        System.out.println();
        System.out.println("Starting to merge");
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();

        MergeAllIndices();

        stopWatch1.stop();
        System.out.println("Finished merging");
        double time1 = stopWatch1.getTime() / 60000.0;
        double seconds1 = time1 - (int)time1;
        System.out.println("Merging time = " + (int)time1 + " minutes and " + (int)(seconds1*60) + " seconds");
        System.out.println();
        System.out.println();
        double totalTime = time+time1;
        double totalSeconds =  totalTime - (int)totalTime;
        System.out.println("Total time = " + (int)totalTime + " minutes and " + (int)(totalSeconds*60) + " seconds");
        System.out.println();
        System.out.println();

        System.out.println("Starting to save for each document it's five big words");
        StopWatch stopWatchForSavingFiveBigWordsForEachDocument = new StopWatch();
        stopWatchForSavingFiveBigWordsForEachDocument.start();

        saveFiveBigWordsForEachDocumentAndMakeTheCossimCalculation();

        stopWatchForSavingFiveBigWordsForEachDocument.stop();
        System.out.println("Finished saving for each document it's five big words");
        double timeFive = stopWatchForSavingFiveBigWordsForEachDocument.getTime() / 60000.0;
        double secondsFive = timeFive - (int)timeFive;
        System.out.println("Time = " + (int)timeFive + " minutes and " + (int)(secondsFive*60) + " seconds");

    }

    protected abstract void saveFiveBigWordsForEachDocumentAndMakeTheCossimCalculation();

    public void SetDestinationPath(String destPath) {
        destPathForTempIndices = destPath + '\\' + "tempIndices";
        SaveIndexToDisk.setFolder(destPathForTempIndices);
        SaveIndexToDisk.initialize();
        destPathForTotalIndices = destPath + '\\' + "totalIndices";

        String smallFolder = destPathForTotalIndices + "\\smallLetters";
        String bigFolder = destPathForTotalIndices + "\\bigLetters";
        String cityFolder = destPathForTotalIndices + "\\cities";
        String numFolder = destPathForTotalIndices + "\\numbers";
        String rangeOrPhraseFolder = destPathForTotalIndices + "\\rangeOrPhrase";
        String percentageFolder = destPathForTotalIndices + "\\percentage";
        String priceFolder = destPathForTotalIndices + "\\price";
        String dateFolder = destPathForTotalIndices + "\\date";

        String documentsFolder = destPathForTotalIndices + "\\documents";

        File directory = new File(destPathForTotalIndices);

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

        File documents = new File(documentsFolder);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!small.exists()) {
            small.mkdir();
        }
        if (!big.exists()) {
            big.mkdir();
        }
        for(int i = 0; i < smallLetters.length; i++){
            if(!smallLetters[i].exists())
                smallLetters[i].mkdir();
            if(!bigLetters[i].exists())
                bigLetters[i].mkdir();
        }

        if (!city.exists()) {
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

        if (!documents.exists()) {
            documents.mkdir();
        }
    }

    protected abstract void MergeAllIndices();

    protected abstract void startReadingFiles(String path);

    protected abstract void startParsing();

    protected abstract void CreateStopWords(File file);

    public void SetStopWords(File file){
        CreateStopWords(file);
    }
    public void setStem(boolean toStem){
        stem = toStem;
    }
}
