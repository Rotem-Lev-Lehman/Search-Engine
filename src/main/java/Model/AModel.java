package Model;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.*;

/**
 * An abstract model class, all of the functionality that the model allows are in these functions
 */
public abstract class AModel {
    /**
     * The ReadFile class that can return a list of all the Documents in the path given
     */
    protected IReadFile readFile;
    protected IParse parser;
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
    protected AIndex[] smallLetterIndexers;
    protected AIndex[] bigLetterIndexers;
    protected AIndex[] cityIndexers;

    /** Creates all of the Documents in the given path
     * @param path - The path where all of the Documents are in
     */
    public void GetAllDocuments(String path){
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
        startIndexing();
        startParsing();

        while (!finishedSmallLetterIndexing);
        while (!finishedBigLetterIndexing);
        //while (!finishedCityIndexing);

        stopWatch.stop();
        double time = stopWatch.getTime() / 60000.0;
        double seconds = time - (int)time;
        System.out.println("Total time = " + (int)time + " minutes and " + (int)(seconds*60) + " seconds");
        //System.out.println(documents.size());
    }

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
