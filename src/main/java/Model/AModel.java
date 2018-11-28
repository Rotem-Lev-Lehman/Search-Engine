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
    protected volatile boolean finishedRetrivingFiles;
    protected volatile Object lock = new Object();
    protected HashSet<String> stopWords;
    protected volatile Queue<MyTuple> indexQueue;
    protected volatile boolean finishedParsing;
    protected volatile Object indexLock = new Object();
    protected volatile boolean finishedIndexing;
    protected AIndex index;

    /** Creates all of the Documents in the given path
     * @param path - The path where all of the Documents are in
     */
    public void GetAllDocuments(String path){
        StopWatch stopWatch = new StopWatch();
        finishedRetrivingFiles = false;
        finishedParsing = false;
        finishedIndexing = false;
        documents = new ArrayDeque<Document>();
        indexQueue = new ArrayDeque<>();

        stopWatch.start();

        startReadingFiles(path);
        startIndexing();
        startParsing();

        while (!finishedIndexing);
        SaveIndexToDisk saveIndexToDisk = new SaveIndexToDisk();
        saveIndexToDisk.save(index);

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
