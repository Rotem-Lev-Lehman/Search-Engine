package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A model class that represents the entire model of our Search Engine
 */
public class Model extends AModel {
    /**
     * An empty constructor for the model
     */
    public Model() {
        readFile = new ReadFile(new DocumentFactory());
        parser = new Parse();
    }

    private void initializeAllSmallLetterIndexers() {
        for (int i = 0; i < smallLetterIndexers.length; i++) {
            smallLetterIndexers[i] = new TermsIndex();
        }
    }

    private void initializeAllBigLetterIndexers() {
        for (int i = 0; i < bigLetterIndexers.length; i++) {
            bigLetterIndexers[i] = new TermsIndex();
        }
    }

    private void initializeAllCityIndexers() {
        for (int i = 0; i < cityIndexers.length; i++) {
            cityIndexers[i] = new TermsIndex();
        }
    }

    @Override
    protected void startIndexing() {
        smallLetterIndexers = new AIndex[Runtime.getRuntime().availableProcessors()];
        bigLetterIndexers = new AIndex[Runtime.getRuntime().availableProcessors()];
        //cityIndexers = new AIndex[Runtime.getRuntime().availableProcessors()];

        initializeAllSmallLetterIndexers();
        initializeAllBigLetterIndexers();
        //initializeAllCityIndexers();

        AIndexMerger smallLetterIndexMerger = new TermsIndexMerger();
        AIndexMerger bigLetterIndexMerger = new TermsIndexMerger();
        //AIndexMerger cityIndexMerger = new CityIndexMerger();

        IndexStarter smallLetterIndexStarter = new IndexStarter(smallLetterIndexers, smallLetterIndexQueue, smallLetterIndexLock, smallLetterIndexMerger, TypeOfIndex.SmallLetters);
        Thread smallLetterThread = new Thread(smallLetterIndexStarter);
        smallLetterThread.start();

        IndexStarter bigLetterIndexStarter = new IndexStarter(bigLetterIndexers, bigLetterIndexQueue, bigLetterIndexLock, bigLetterIndexMerger, TypeOfIndex.BigLetters);
        Thread bigLetterThread = new Thread(bigLetterIndexStarter);
        bigLetterThread.start();

        //IndexStarter cityIndexStarter = new IndexStarter(cityIndexers, cityIndexQueue, cityIndexLock, cityIndexMerger, TypeOfIndex.City);
        //Thread cityThread = new Thread(cityIndexStarter);
        //cityThread.start();
    }

    private class IndexStarter implements Runnable{
        private AIndex[] indices;
        private Queue<MyTuple> indicesQueue;
        private Object indicesLock;
        private AIndexMerger merger;
        private TypeOfIndex typeOfIndex;

        public IndexStarter(AIndex[] indices, Queue<MyTuple> indicesQueue, Object indicesLock, AIndexMerger merger, TypeOfIndex typeOfIndex){
            this.indices = indices;
            this.indicesLock = indicesLock;
            this.indicesQueue = indicesQueue;
            this.merger = merger;
            this.typeOfIndex = typeOfIndex;
        }

        @Override
        public void run() {
            int amountOfDocs = 0;
            int currentIndex = 0;
            ExecutorService threadPool = Executors.newFixedThreadPool(indices.length);
            while (!finishedParsing || indicesQueue.size() != 0) {
                MyTuple tuple;
                synchronized (indicesLock) {
                    tuple = indicesQueue.poll();
                }
                if (tuple == null) {
                    try {
                        Thread.sleep(10); // minimize busy waiting
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                IndexerThread indexerThread = new IndexerThread(tuple, indices[currentIndex]);
                currentIndex = (currentIndex + 1) % indices.length; // the next index to use
                amountOfDocs++;
                threadPool.submit(indexerThread);
                //System.out.println("current file = " + tuple.getDocument().getFilename());

                if (amountOfDocs > 3500) { //approximately 10 MB
                    //start counting again
                    amountOfDocs = 0;
                    //wait for all threads to finish indexing
                    threadPool.shutdown();
                    try {
                        boolean done = false;
                        while (!done)
                            done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //merge the indices
                    AIndex merged = merger.Merge(indices);
                    merged.setType(typeOfIndex);

                    //save the merged index
                    ThreadIndexSaver threadIndexSaver = new ThreadIndexSaver(merged);
                    Thread indexSaver = new Thread(threadIndexSaver);
                    indexSaver.start();

                    //create the new indices
                    for (AIndex index : indices) {
                        index.ClearIndex();
                    }

                    //start again
                    threadPool = Executors.newFixedThreadPool(indices.length);
                }
            }
            threadPool.shutdown();
            try {
                boolean done = false;
                while (!done)
                    done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //merge the indices
            AIndex merged = merger.Merge(indices);
            merged.setType(typeOfIndex);

            if(!merged.isEmpty()) {
                //save the merged index
                ThreadIndexSaver threadIndexSaver = new ThreadIndexSaver(merged);
                Thread indexSaver = new Thread(threadIndexSaver);
                indexSaver.start();
            }

            if(typeOfIndex == TypeOfIndex.SmallLetters)
                finishedSmallLetterIndexing = true;
            else if(typeOfIndex == TypeOfIndex.BigLetters)
                finishedBigLetterIndexing = true;
            else
                finishedCityIndexing = true;
        }
    }

    private class ThreadIndexSaver implements Runnable{
        private AIndex index;

        public ThreadIndexSaver(AIndex index) {
            this.index = index;
        }

        @Override
        public void run() {
            SaveIndexToDisk saveIndexToDisk = new SaveIndexToDisk();
            saveIndexToDisk.save(index);
        }
    }

    @Override
    protected void startReadingFiles(String path) {
        ReaderThread readerThread = new ReaderThread(path);
        Thread reader = new Thread(readerThread);
        reader.start();
    }

    private class IndexerThread implements Runnable {

        private MyTuple tuple;
        private AIndex index;

        public IndexerThread(MyTuple tuple, AIndex index){
            this.tuple = tuple;
            this.index = index;
        }

        @Override
        public void run() {
            //index.addDocumentToIndex(tuple.getTerms(), tuple.getDocument());
        }
    }

    private class ReaderThread implements Runnable {

        private String path;

        public ReaderThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            //readFile.ReadFile(path, documents, lock);
            finishedRetrievingFiles = true;
        }
    }

    @Override
    protected void CreateStopWords(File file) {
        String content = ReadAGivenFile(file);
        stopWords = new HashSet<String>();
        String[] words = content.split("\n");
        List<String> w = Arrays.asList(words);

        stopWords.addAll(w);
    }

    /**
     * Reads a given file
     *
     * @param file - The file that needs to be read
     * @return The file's content
     */
    private String ReadAGivenFile(File file) {
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    @Override
    protected void startParsing() {
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int totalDocsWaiting=0;
        while (!finishedRetrievingFiles || documents.size() != 0) {
            synchronized (smallLetterIndexLock)
            {
                totalDocsWaiting += smallLetterIndexQueue.size();
            }
            synchronized (bigLetterIndexLock){
                totalDocsWaiting += bigLetterIndexQueue.size();
            }
            synchronized (cityIndexLock){
                totalDocsWaiting += cityIndexQueue.size();
            }
            if(totalDocsWaiting > 100) {
                try {
                    Thread.sleep(10); // give the Indexers a chance to control the situation
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Document currentDoc;
            synchronized (lock) {
                currentDoc = documents.poll();
            }
            if (currentDoc == null) {
                try {
                    Thread.sleep(10); // give a chance to the reader thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            ParserThread parserThread = new ParserThread(currentDoc);
            threadPool.submit(parserThread);

            //System.out.println("current file = " + currentDoc.getFilename());
        }
        threadPool.shutdown();
        try {
            boolean done = false;
            while (!done)
                done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finishedParsing = true;
    }

    private class ParserThread implements Runnable {

        private Document document;

        public ParserThread(Document document) {
            this.document = document;
        }

        @Override
        public void run() {
            Parse parse = new Parse();
            List<Term> terms = parse.Parse(document, stopWords);
            SplitAndIndex(document, terms);
        }
    }

    private void SplitAndIndex(Document document, List<Term> terms) {
        //Split
        List<Term> smallLetterTerms = new ArrayList<Term>();
        List<Term> bigLetterTerms = new ArrayList<Term>();
        List<Term> cityTerms = new ArrayList<Term>();
        for (Term term : terms) {
            if(term.getType() == TypeOfTerm.BigLetters)
                bigLetterTerms.add(term);
            else if(term.getType() == TypeOfTerm.City)
                cityTerms.add(term);
            else //if its a small letter term or a special term
                smallLetterTerms.add(term);
        }
        //Index
        synchronized (smallLetterIndexLock){
            smallLetterIndexQueue.add(new MyTuple(document,smallLetterTerms));
        }
        synchronized (bigLetterIndexLock){
            bigLetterIndexQueue.add(new MyTuple(document,bigLetterTerms));
        }
        synchronized (cityIndexLock){
            //cityIndexQueue.add(new MyTuple(document,cityTerms));
        }
    }
}
