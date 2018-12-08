package Model;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Model extends AModel {

    private int amountOfDocsAllowedInQueue = 300;
    private int amountOfParsedDocsInRam = 1000;
    private int amountOfThreadsInThreadPool = (2*(Runtime.getRuntime().availableProcessors())) - 1;
    private int amountOfTasksAllowedInThreadPool = amountOfThreadsInThreadPool;
    private int maxAmountOfIndicesInTheMerger = 20;

    public Model(){
        readFile = new ReadFile(new DocumentFactory());
        empty = new Semaphore(0, true);
        full = new Semaphore(amountOfDocsAllowedInQueue, true); // change permits to be good with Memory space and timing
        tasksLimit = new Semaphore(amountOfTasksAllowedInThreadPool, true);
    }

    @Override
    protected void MergeAllIndices() {
        String smallPath = destPathForTempIndices + "\\smallLetters";
        String bigPath = destPathForTempIndices + "\\bigLetters";
        String cityPath = destPathForTempIndices + "\\cities";

        String totalSmallPath = destPathForTotalIndices + "\\smallLetters";
        String totalBigPath = destPathForTotalIndices + "\\bigLetters";
        String totalCityPath = destPathForTotalIndices + "\\cities";

        String tempSmallPath = destPathForTempIndices + "\\onlySmallLetters";
        String tempBigPath = destPathForTempIndices + "\\onlyBigLetters";

        File smallDir = new File(smallPath);
        File bigDir = new File(bigPath);
        File cityDir = new File(cityPath);

        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MergerThread(smallDir, tempSmallPath, TypeOfIndex.SmallLetters));
        threads[1] = new Thread(new MergerThread(bigDir, tempBigPath, TypeOfIndex.BigLetters));
        threads[2] = new Thread(new MergerThread(cityDir, totalCityPath, TypeOfIndex.City));

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

        //merge small letters and big letters (after each of them where merged individually
        BigSmallMerger merger;
        merger = new BigSmallMerger(tempBigPath, tempSmallPath, totalBigPath, totalSmallPath);
        merger.MergeAll();

        //were done with the temp indices, so delete them...
        deleteDirectory(new File(destPathForTempIndices));
    }

    private class MergerThread implements Runnable{
        private File directory;
        private String totalPath;
        private TypeOfIndex type;
        private String tempPath;

        public MergerThread(File directory, String totalPath, TypeOfIndex type){
            this.directory = directory;
            this.totalPath = totalPath;
            this.type = type;
            this.tempPath = totalPath + "\\temp";
        }

        @Override
        public void run() {
            File[] files = directory.listFiles();

            int layers = (int)Math.ceil(Math.log(files.length) / Math.log(maxAmountOfIndicesInTheMerger)); // log of length in base of max

            for(int layerIndex = 0; layerIndex < layers; layerIndex++) {
                String layerDirectoryName;
                if (layerIndex == layers - 1)
                    layerDirectoryName = this.totalPath;
                else
                    layerDirectoryName = tempPath + '\\' + layerIndex;

                int next = 0;
                int length = Math.min(maxAmountOfIndicesInTheMerger, files.length - next);

                int indexOfCurrent = 0;

                while (length > 0) {
                    String[] current = new String[length];
                    for (int i = 0; i < length; i++) {
                        current[i] = files[next + i].getAbsolutePath();
                    }

                    String currentDir = layerDirectoryName;
                    if (layerIndex < layers - 1)
                        currentDir += "\\" + indexOfCurrent;


                    File curr = new File(currentDir);

                    if (!curr.exists()){
                        curr.mkdirs();
                    }

                    AMerger merger;
                    if (type == TypeOfIndex.City)
                        merger = new CityMerger(current, currentDir);
                    else
                        merger = new TermsMerger(current, currentDir);
                    merger.MergeAll();

                    next += length;
                    length = Math.min(maxAmountOfIndicesInTheMerger, files.length - next);

                    indexOfCurrent++;
                }

                File nextDir = new File(layerDirectoryName);
                files = nextDir.listFiles();
            }

            //delete the temp files after we have merged successfully
            deleteDirectory(new File(tempPath));
        }
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private class IndexerThread implements Runnable {

        private List<Term> terms;
        private AIndex index;
        private CityInfo info;
        private int length;

        Semaphore maxTfCalculatorSemaphore;
        Semaphore maxTfUpdateSemaphore;
        Object lock;
        MyInteger tf;
        MyInteger uniqueTermsNum;
        MyInteger docId;

        public IndexerThread(List<Term> terms, int length, CityInfo info, AIndex index, Semaphore maxTfCalculatorSemaphore, Semaphore maxTfUpdateSemaphore, Object lock, MyInteger tf, MyInteger uniqueTermsNum, MyInteger docId){
            this.terms = terms;
            this.index = index;
            this.info = info;
            this.length = length;

            this.maxTfCalculatorSemaphore = maxTfCalculatorSemaphore;
            this.maxTfUpdateSemaphore = maxTfUpdateSemaphore;
            this.lock = lock;
            this.tf = tf;
            this.uniqueTermsNum = uniqueTermsNum;
            this.docId = docId;
        }

        @Override
        public void run() {
            index.addDocumentToIndex(terms, info, length, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docId);
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
        try {
            stopWords = new HashSet<String>();
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
            while (scanner.hasNext())
                stopWords.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void startParsing() {
        smallLetterIndexer = new TermsIndex();
        smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
        bigLetterIndexer = new TermsIndex();
        bigLetterIndexer.setType(TypeOfIndex.BigLetters);
        cityIndexer = new CityIndex();
        cityIndexer.setType(TypeOfIndex.City);

        documentsDictionary = new DocumentsDictionary(destPathForTotalIndices + "\\documents");

        ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
        ExecutorService savers = Executors.newCachedThreadPool();
        int amountOfDocs = 0;
        while (!finishedRetrievingFiles || documents.size() != 0) {
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

                //System.out.println("Saving the total big letters index");
                ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer);
                savers.submit(bigLetterIndex);

                ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
                savers.submit(cityIndex);

                //System.out.println("Creating new indices");
                //create the new indices
                smallLetterIndexer = new TermsIndex();
                smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
                bigLetterIndexer = new TermsIndex();
                bigLetterIndexer.setType(TypeOfIndex.BigLetters);
                cityIndexer = new CityIndex();
                cityIndexer.setType(TypeOfIndex.City);

                //System.out.println("Restarting the pool");
                //restart
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);

                System.out.println("current file = " + currentDoc.getFilename());
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

        //save the index
        ThreadIndexSaver smallLetterIndex = new ThreadIndexSaver(smallLetterIndexer);
        savers.submit(smallLetterIndex);

        ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer);
        savers.submit(bigLetterIndex);

        ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
        savers.submit(cityIndex);

        documentsDictionary.close();

        savers.shutdown();
        try {
            boolean done = false;
            while (!done)
                done = savers.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        smallLetterIndexer = null;
        bigLetterIndexer = null;
        cityIndexer = null;
    }

    private class ParserThread implements Runnable {

        private Document document;

        public ParserThread(Document document) {
            this.document = document;
        }

        @Override
        public void run() {
            Parse parse = new Parse();
            List<Term> terms = parse.Parse(document, stopWords, stem);
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
            else //if it's a small letter term or a special term
                smallLetterTerms.add(term);
        }

        //Ensure that there will not be a small letter and a big letter term of the same String in the same document.
        int bigSize = bigLetterTerms.size();
        for(int i = 0; i < bigSize; i++){
            Term big = bigLetterTerms.get(i);
            boolean found = false;
            for(Term term : smallLetterTerms){
                if(term.getValue().equals(big.getValue().toLowerCase()))
                    found = true;
            }
            if(found) {
                big.setType(TypeOfTerm.SmallLetters);
                big.setValue(big.getValue().toLowerCase());
                smallLetterTerms.add(big);
                bigLetterTerms.remove(i);
                i--; //check the next big letter
                bigSize = bigLetterTerms.size();
            }
        }

        Semaphore maxTfCalculatorSemaphore = new Semaphore(0, true);
        Semaphore maxTfUpdateSemaphore = new Semaphore(0, true);
        int permits = 0;
        Object lock = new Object();
        MyInteger tf = new MyInteger(0);
        MyInteger uniqueTermsNum = new MyInteger(0);
        MyInteger docIndex = new MyInteger(0);

        //Index
        Thread[] threads = new Thread[3];
        if(smallLetterTerms.size() > 0){
            IndexerThread small = new IndexerThread(smallLetterTerms, terms.size(), document.getCityInfo(), smallLetterIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(small);
            thread.start();
            threads[0] = thread;

            permits++;
        }
        else
            threads[0] = null;

        if(bigLetterTerms.size() > 0){
            IndexerThread big = new IndexerThread(bigLetterTerms, terms.size(), document.getCityInfo(), bigLetterIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(big);
            thread.start();
            threads[1] = thread;

            permits++;
        }
        else
            threads[1] = null;

        if(cityTerms.size() > 0){
            IndexerThread city = new IndexerThread(cityTerms, terms.size(), document.getCityInfo(), cityIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(city);
            thread.start();
            threads[2] = thread;

            permits++;
        }
        else
            threads[2] = null;

        //wait for all of the threads to calculate their max tf:
        try {
            maxTfUpdateSemaphore.acquire(permits);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        docIndex.setValue(documentsDictionary.insert(new DocumentsDictionaryEntrance(document.getDOCNO(),document.getFilename(),uniqueTermsNum.getValue(),tf.getValue(),document.getCity())));

        //tell all of the threads that they may continue, because that all of them has gotten to this point:
        maxTfCalculatorSemaphore.release(permits);

        for (int i = 0; i < threads.length; i++){
            if(threads[i] == null)
                continue;
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
