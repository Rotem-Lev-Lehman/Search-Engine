package Model;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public abstract class AModel2 {
    /**
     * The ReadFile class that can return a list of all the Documents in the path given
     */
    protected IReadFile readFile;
    /**
     * The Documents of the model
     */
    //protected List<Document> documents;

    protected volatile Queue<Document> documents;
    protected volatile boolean finishedRetrievingFiles;
    protected volatile Object lock = new Object();
    protected HashSet<String> stopWords;
    protected volatile Queue<MyTuple> smallLetterIndexQueue;
    protected volatile Queue<MyTuple> bigLetterIndexQueue;
    protected volatile Queue<MyTuple> cityIndexQueue;
    protected volatile boolean finishedParsing;
    protected volatile Object smallLetterIndexLock = new Object();
    protected volatile Object bigLetterIndexLock = new Object();
    protected volatile Object cityIndexLock = new Object();
    protected volatile boolean finishedSmallLetterIndexing;
    protected volatile boolean finishedBigLetterIndexing;
    protected volatile boolean finishedCityIndexing;
    protected AIndex smallLetterIndexer;
    protected AIndex bigLetterIndexer;
    protected AIndex cityIndexer;
    protected Semaphore empty;
    protected Semaphore full;
    protected Semaphore tasksLimit;
    protected String destPathForTotalIndices;
    protected String destPathForTempIndices;

    /** Creates all of the Documents in the given path
     * @param path - The path where all of the Documents are in
     */
    public void GetAllDocuments(String path){
        SetDestinationPath("C:\\Users\\User\\Desktop\\אחזור מידע\\indices");

        StopWatch stopWatch = new StopWatch();
        finishedRetrievingFiles = false;
        finishedParsing = false;
        finishedSmallLetterIndexing = false;
        finishedBigLetterIndexing = false;
        finishedCityIndexing = false;
        documents = new ArrayDeque<Document>();
        smallLetterIndexQueue = new ArrayDeque<>();
        bigLetterIndexQueue = new ArrayDeque<MyTuple>();
        cityIndexQueue = new ArrayDeque<MyTuple>();

        stopWatch.start();

        startReadingFiles(path);
        startParsing();

        stopWatch.stop();
        System.out.println("Finished parsing");
        double time = stopWatch.getTime() / 60000.0;
        double seconds = time - (int)time;
        System.out.println("Total time = " + (int)time + " minutes and " + (int)(seconds*60) + " seconds");
        //System.out.println(documents.size());
        System.out.println("documents = " + documents.size());
        System.out.println("small letters indexers queue = "+smallLetterIndexQueue.size());
        System.out.println("big letters indexers queue = "+bigLetterIndexQueue.size());
        System.out.println("city indexers queue = "+cityIndexQueue.size());
        System.out.println("small letter index(dic) = " + smallLetterIndexer.getDictionary().getMap().size());
        System.out.println("small letter index(post) = " + smallLetterIndexer.getPosting().getPostingList().size());
        System.out.println("big letter index(dic) = " + bigLetterIndexer.getDictionary().getMap().size());
        System.out.println("big letter index(post) = " + bigLetterIndexer.getPosting().getPostingList().size());
        System.out.println("city index(dic) = " + cityIndexer.getDictionary().getMap().size());
        System.out.println("city index(post) = " + cityIndexer.getPosting().getPostingList().size());

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
        double time1 = stopWatch.getTime() / 60000.0;
        double seconds1 = time1 - (int)time1;
        System.out.println("Merging time = " + (int)time1 + " minutes and " + (int)(seconds1*60) + " seconds");
        System.out.println();
        System.out.println();
        double totalTime = time+time1;
        double totalSeconds =  totalTime - (int)totalTime;
        System.out.println("Total time = " + (int)totalTime + " minutes and " + (int)(totalSeconds*60) + " seconds");
    }

    public void SetDestinationPath(String destPath){
        destPathForTempIndices = destPath + '\\' + "tempIndices";
        SaveIndexToDisk.setFolder(destPathForTempIndices);
        destPathForTotalIndices = destPath + '\\' + "totalIndices";
        String smallFolder = destPathForTotalIndices + "\\smallLetters";
        String bigFolder = destPathForTotalIndices + "\\bigLetters";
        String cityFolder = destPathForTotalIndices + "\\cities";

        File directory = new File(destPathForTotalIndices);
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

    protected abstract void MergeAllIndices();

    protected abstract void startIndexing();

    protected abstract void startReadingFiles(String path);

    protected abstract void startParsing();

    protected abstract void CreateStopWords(File file);

    public void SetStopWords(File file){
        CreateStopWords(file);
    }

    /** Gets the Document at the given index
     * @param index - The index of the wanted Document
     * @return The Document at the given index
     */
    //public Document GetDocumentAt(int index){
    // return documents.get(index);
    // }
}
