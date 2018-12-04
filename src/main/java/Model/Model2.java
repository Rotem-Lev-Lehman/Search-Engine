package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Model2 extends AModel2 {

    private int amountOfDocsAllowedInQueue = 300;
    private int amountOfParsedDocsInRam = 10000; //approximately 10 MB (17000)
    private int amountOfThreadsInThreadPool = (2*(Runtime.getRuntime().availableProcessors())) - 1;
    private int amountOfTasksAllowedInThreadPool = amountOfThreadsInThreadPool + 4;

    public Model2(){
        readFile = new ReadFile(new DocumentFactory());
        empty = new Semaphore(0, true);
        full = new Semaphore(amountOfDocsAllowedInQueue, true); // change permits to be good with Memory space and timing
        tasksLimit = new Semaphore(amountOfTasksAllowedInThreadPool, true);
        smallLetterIndexer = new TermsIndex();
        smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
        bigLetterIndexer = new TermsIndex();
        bigLetterIndexer.setType(TypeOfIndex.BigLetters);
        cityIndexer = new CityIndex();
        cityIndexer.setType(TypeOfIndex.City);
    }

    @Override
    protected void MergeAllIndices() {
        String smallPath = destPathForTempIndices + "\\smallLetters";
        String bigPath = destPathForTempIndices + "\\bigLetters";
        String cityPath = destPathForTempIndices + "\\cities";

        String totalSmallPath = destPathForTotalIndices + "\\smallLetters";
        String totalBigPath = destPathForTotalIndices + "\\bigLetters";
        String totalCityPath = destPathForTotalIndices + "\\cities";

        File smallDir = new File(smallPath);
        File bigDir = new File(bigPath);
        File cityDir = new File(cityPath);

        Thread[] threads = new Thread[2];
        //Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MergerThread(smallDir, totalSmallPath, TypeOfIndex.SmallLetters));
        threads[1] = new Thread(new MergerThread(bigDir, totalBigPath, TypeOfIndex.BigLetters));
        //threads[2] = new Thread(new MergerThread(cityDir, totalCityPath, TypeOfIndex.City));

        // start merging
        for (int i = 0; i < threads.length; i++)
            threads[i].start();

        //wait for all mergers to finish
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MergerThread implements Runnable{
        private File directory;
        private String totalPath;
        private TypeOfIndex type;

        public MergerThread(File directory, String totalPath, TypeOfIndex type){
            this.directory = directory;
            this.totalPath = totalPath;
            this.type = type;
        }

        @Override
        public void run() {
            File[] files = directory.listFiles();

            String[] filenames = new String[files.length];
            for (int i = 0; i < files.length; i++)
                filenames[i] = files[i].getAbsolutePath();

            AMerger merger;
            if(type == TypeOfIndex.City)
                merger = new CityMerger(filenames, totalPath);
            else
                merger = new TermsMerger(filenames, totalPath);
            merger.MergeAll();
        }
    }

    @Override
    protected void startIndexing() {
        smallLetterIndexer = new TermsIndex();
        smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
        bigLetterIndexer = new TermsIndex();
        bigLetterIndexer.setType(TypeOfIndex.BigLetters);
        cityIndexer = new CityIndex();
        cityIndexer.setType(TypeOfIndex.City);

        IndexStarter smallLetterIndexStarter = new IndexStarter(smallLetterIndexer, smallLetterIndexQueue, smallLetterIndexLock, TypeOfIndex.SmallLetters);
        Thread smallLetterThread = new Thread(smallLetterIndexStarter);
        smallLetterThread.start();

        IndexStarter bigLetterIndexStarter = new IndexStarter(bigLetterIndexer, bigLetterIndexQueue, bigLetterIndexLock, TypeOfIndex.BigLetters);
        Thread bigLetterThread = new Thread(bigLetterIndexStarter);
        bigLetterThread.start();

        //Model.Model2.IndexStarter cityIndexStarter = new Model.Model2.IndexStarter(cityIndexers, cityIndexQueue, cityIndexLock, TypeOfIndex.City);
        //Thread cityThread = new Thread(cityIndexStarter);
        //cityThread.start();
    }

    private class IndexStarter implements Runnable{
        private AIndex index;
        private Queue<MyTuple> indexQueue;
        private Object indexLock;
        private TypeOfIndex typeOfIndex;

        public IndexStarter(AIndex index, Queue<MyTuple> indexQueue, Object indexLock, TypeOfIndex typeOfIndex){
            this.index = index;
            this.indexLock = indexLock;
            this.indexQueue = indexQueue;
            this.typeOfIndex = typeOfIndex;
        }

        @Override
        public void run() {
            int amountOfDocs = 0;
            ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            while (!finishedParsing || indexQueue.size() != 0) {
                MyTuple tuple;
                synchronized (indexLock) {
                    tuple = indexQueue.poll();
                }
                if (tuple == null) {
                    try {
                        Thread.sleep(10); // minimize busy waiting
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                IndexerThread indexerThread = new IndexerThread(tuple, index);
                //index.addDocumentToIndex(tuple.getTerms(), tuple.getDocument());
                amountOfDocs++;
                //System.out.println("current file = " + tuple.getFilename());
                threadPool.submit(indexerThread);

                if (amountOfDocs > 5000) { //approximately 10 MB
                    //start counting again
                    amountOfDocs = 0;

                    //wait for the indexing to finish
                    threadPool.shutdown();
                    try {
                        boolean done = false;
                        while (!done)
                            done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //save the index
                    ThreadIndexSaver threadIndexSaver = new ThreadIndexSaver(index);
                    Thread indexSaver = new Thread(threadIndexSaver);
                    indexSaver.start();

                    //create the new indices
                    if(typeOfIndex == TypeOfIndex.City)
                        index = new CityIndex();
                    else
                        index = new TermsIndex();
                    index.setType(typeOfIndex);

                    //restart
                    threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                }
            }
            //wait for the indexing to finish
            threadPool.shutdown();
            try {
                boolean done = false;
                while (!done)
                    done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!index.isEmpty()){
                //save the index
                ThreadIndexSaver threadIndexSaver = new ThreadIndexSaver(index);
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

    private class IndexerThread implements Runnable {

        private MyTuple tuple;
        private AIndex index;

        public IndexerThread(MyTuple tuple, AIndex index){
            this.tuple = tuple;
            this.index = index;
        }

        @Override
        public void run() {
            index.addDocumentToIndex(tuple.getTerms(), tuple.getDocNo(), tuple.getFilename());
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

    private class ReaderThread implements Runnable {

        private String path;

        public ReaderThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            readFile.ReadFile(path, documents, lock, empty, full);
            finishedRetrievingFiles = true;

            empty.release(); // signal to the parser that we have finished reading
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
        ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
        ExecutorService savers = Executors.newCachedThreadPool();
        //int bias = 500;
        int amountOfDocs = 0;
        while (!finishedRetrievingFiles || documents.size() != 0) {
            /*
            int totalDocsWaiting=0;
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
            if(totalDocsWaiting > bias) {
                try {
                    Thread.sleep(50); // give the Indexer a chance to control the situation
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bias /= 2;
                if(bias < 100)
                    bias = 500;
                continue;
            }
            bias = 500;
            */

            Document currentDoc;
            try {
                empty.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                currentDoc = documents.poll();
            }
            full.release();

            if(currentDoc == null)
            {
                //done signal...
                break;
            }

            //block until free
            try {
                tasksLimit.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            if (currentDoc == null) {
                try {
                    Thread.sleep(10); // give a chance to the reader thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            */
            ParserThread parserThread = new ParserThread(currentDoc);
            threadPool.submit(parserThread);

            amountOfDocs++;

            if (amountOfDocs > amountOfParsedDocsInRam) {
                //System.out.println("Starting to save");

                //start counting again
                amountOfDocs = 0;

                //System.out.println("Waiting for indexing threads to finish");
                //wait for the indexing to finish
                threadPool.shutdown();
                try {
                    boolean done = false;
                    while (!done)
                        done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println("Saving the total small letters index");
                //save the index
                ThreadIndexSaver smallLetterIndex = new ThreadIndexSaver(smallLetterIndexer);
                savers.submit(smallLetterIndex);
                //Thread indexSaver = new Thread(smallLetterIndex);
                //indexSaver.start();

                //System.out.println("Saving the total big letters index");
                ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer);
                savers.submit(bigLetterIndex);
                //Thread bigSaver = new Thread(bigLetterIndex);
                //bigSaver.start();

                /*
                ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
                savers.submit(cityIndex);
                //Thread citySaver = new Thread(cityIndex);
                //citySaver.start();
                */
                //System.gc();

                //System.out.println("Creating new indices");
                //create the new indices
                smallLetterIndexer = new TermsIndex();
                smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
                bigLetterIndexer = new TermsIndex();
                bigLetterIndexer.setType(TypeOfIndex.BigLetters);
                cityIndexer = new CityIndex();
                cityIndexer.setType(TypeOfIndex.City);

                //System.gc(); // lets try just for fun ;)

                //System.out.println("Restarting the pool");
                //restart
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
            }

            //System.out.println("current file = " + currentDoc.getFilename());
            //System.out.println("small letter index(dic) = " + smallLetterIndexer.getDictionary().getMap().size());
            //System.out.println("small letter index(post) = " + smallLetterIndexer.getPosting().getPostingList().size());
        }
        threadPool.shutdown();
        try {
            boolean done = false;
            while (!done)
                done = threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //save the index
        ThreadIndexSaver smallLetterIndex = new ThreadIndexSaver(smallLetterIndexer);
        savers.submit(smallLetterIndex);
        //Thread indexSaver = new Thread(smallLetterIndex);
        //indexSaver.start();

        ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer);
        savers.submit(bigLetterIndex);
        //Thread bigSaver = new Thread(bigLetterIndex);
        //bigSaver.start();

        /*
        ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
        savers.submit(cityIndex);
        //Thread citySaver = new Thread(cityIndex);
        //citySaver.start();
        */

        savers.shutdown();
        try {
            boolean done = false;
            while (!done)
                done = savers.awaitTermination(1000, TimeUnit.MILLISECONDS);
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

            //tell the threadPool's controller that it can send in another thread
            tasksLimit.release();
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
        Thread[] threads = new Thread[2];
        //Thread[] threads = new Thread[3]; with city
        MyTuple tuple = new MyTuple(document,smallLetterTerms);
        if(smallLetterTerms.size() > 0){
            IndexerThread small = new IndexerThread(tuple, smallLetterIndexer);
            Thread thread = new Thread(small);
            thread.start();
            threads[0] = thread;
        }
        else
            threads[0] = null;

        if(bigLetterTerms.size() > 0){
            IndexerThread big = new IndexerThread(tuple, bigLetterIndexer);
            Thread thread = new Thread(big);
            thread.start();
            threads[1] = thread;
        }
        else
            threads[1] = null;

        /*
        if(cityTerms.size() > 0){
            IndexerThread city = new IndexerThread(tuple, cityIndexer);
            Thread thread = new Thread(city);
            thread.start();
            threads[2] = thread;
        }
        else
            threads[2] = null;

        */
        for (int i = 0; i < threads.length; i++){
            if(threads[i] == null)
                continue;
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //old...
        //bigLetterIndexer.addDocumentToIndex(bigLetterTerms,document.getDOCNO(),document.getFilename());
        //if(cityTerms.size() > 0)
            //cityIndexer.addDocumentToIndex(cityTerms,document.getDOCNO(),document.getFilename());


        /* old
        synchronized (smallLetterIndexLock){
            smallLetterIndexQueue.add(new MyTuple(document,smallLetterTerms));
        }
        synchronized (bigLetterIndexLock){
            bigLetterIndexQueue.add(new MyTuple(document,bigLetterTerms));
        }
        synchronized (cityIndexLock){
            //cityIndexQueue.add(new MyTuple(document,cityTerms));
        }
        */
    }


}
