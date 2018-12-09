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
    private int amountOfTermsAllowedInIndex = 1000000;
    private int amountOfThreadsInThreadPool = (2*(Runtime.getRuntime().availableProcessors())) - 1;
    private int amountOfTasksAllowedInThreadPool = amountOfThreadsInThreadPool;
    private int maxAmountOfIndicesInTheMerger = 100;

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
        String numPath = destPathForTempIndices + "\\numbers";
        String rangeOrPhrasePath = destPathForTempIndices + "\\rangeOrPhrase";
        String percentagePath = destPathForTempIndices + "\\percentage";
        String pricePath = destPathForTempIndices + "\\price";
        String datePath = destPathForTempIndices + "\\date";

        String totalSmallPath = destPathForTotalIndices + "\\smallLetters";
        String totalBigPath = destPathForTotalIndices + "\\bigLetters";
        String[] totalSmallLettersPath = new String[26];
        String[] totalBigLettersPath = new String[26];

        String totalCityPath = destPathForTotalIndices + "\\cities";
        String totalNumPath = destPathForTotalIndices + "\\numbers";
        String totalRangeOrPhrasePath = destPathForTotalIndices + "\\rangeOrPhrase";
        String totalPercentagePath = destPathForTotalIndices + "\\percentage";
        String totalPricePath = destPathForTotalIndices + "\\price";
        String totalDatePath = destPathForTotalIndices + "\\date";

        String tempSmallPath = destPathForTempIndices + "\\onlySmallLetters";
        String tempBigPath = destPathForTempIndices + "\\onlyBigLetters";
        String[] tempSmallLettersPath = new String[26];
        String[] tempBigLettersPath = new String[26];

        //File smallDir = new File(smallPath);
        //File bigDir = new File(bigPath);
        File[] smallLettersDir = new File[26];
        File[] bigLettersDir = new File[26];
        for(int i = 0; i < smallLettersDir.length; i++){
            char currLetter = (char)('a' + i);

            totalSmallLettersPath[i] = totalSmallPath + "\\" + currLetter;
            totalBigLettersPath[i] = totalBigPath + "\\" + currLetter;

            tempSmallLettersPath[i] = tempSmallPath + "\\" + currLetter;
            tempBigLettersPath[i] = tempBigPath + "\\" + currLetter;

            smallLettersDir[i] = new File(smallPath + "\\" + currLetter);
            bigLettersDir[i] = new File(bigPath + "\\" + currLetter);
        }

        File cityDir = new File(cityPath);
        File numDir = new File(numPath);
        File rangeOrPhraseDir = new File(rangeOrPhrasePath);
        File percentageDir = new File(percentagePath);
        File priceDir = new File(pricePath);
        File dateDir = new File(datePath);

        MergerThread[] smallMergers = new MergerThread[26];
        for(int i = 0; i < smallMergers.length; i++){
            smallMergers[i] = new MergerThread(smallLettersDir[i],tempSmallLettersPath[i],TypeOfTerm.SmallLetters);
            smallMergers[i].run();
        }

        MergerThread[] bigMergers = new MergerThread[26];
        for(int i = 0; i < smallMergers.length; i++){
            bigMergers[i] = new MergerThread(bigLettersDir[i],tempBigLettersPath[i],TypeOfTerm.BigLetters);
            bigMergers[i].run();
        }

        MergerThread cityMerger = new MergerThread(cityDir,totalCityPath,TypeOfTerm.City);
        cityMerger.run();
        MergerThread numMerger = new MergerThread(numDir,totalNumPath,TypeOfTerm.Number);
        numMerger.run();
        MergerThread rangeOrPhraseMerger = new MergerThread(rangeOrPhraseDir,totalRangeOrPhrasePath,TypeOfTerm.RangeOrPhrase);
        rangeOrPhraseMerger.run();
        MergerThread percentageMerger = new MergerThread(percentageDir,totalPercentagePath,TypeOfTerm.Percentage);
        percentageMerger.run();
        MergerThread priceMerger = new MergerThread(priceDir,totalPricePath,TypeOfTerm.Price);
        priceMerger.run();
        MergerThread dateMerger = new MergerThread(dateDir,totalDatePath,TypeOfTerm.Date);
        dateMerger.run();
        /*
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new MergerThread(smallDir, tempSmallPath, TypeOfTerm.SmallLetters));
        threads[1] = new Thread(new MergerThread(bigDir, tempBigPath, TypeOfTerm.BigLetters));
        threads[2] = new Thread(new MergerThread(cityDir, totalCityPath, TypeOfTerm.City));

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
        */

        //merge small letters and big letters (after each of them where merged individually
        BigSmallMerger[] bigSmallMergers = new BigSmallMerger[26];
        for(int i = 0; i < bigSmallMergers.length; i++){
            bigSmallMergers[i] = new BigSmallMerger(tempBigLettersPath[i], tempSmallLettersPath[i], totalBigLettersPath[i], totalSmallLettersPath[i]);
            bigSmallMergers[i].MergeAll();
        }
        /*
        BigSmallMerger merger;
        merger = new BigSmallMerger(tempBigPath, tempSmallPath, totalBigPath, totalSmallPath);
        merger.MergeAll();
        */

        //were done with the temp indices, so delete them...
        deleteDirectory(new File(destPathForTempIndices));
    }

    private class MergerThread implements Runnable{
        private File directory;
        private String totalPath;
        private TypeOfTerm type;
        private String tempPath;

        public MergerThread(File directory, String totalPath, TypeOfTerm type){
            this.directory = directory;
            this.totalPath = totalPath;
            this.type = type;
            this.tempPath = totalPath + "\\temp";
        }

        @Override
        public void run() {
            File[] files = directory.listFiles();

            if(files.length == 1) {
                //only file so it's already merged
                File[] indexFiles = files[0].listFiles();
                File dest = new File(totalPath);
                if (!dest.exists()){
                    dest.mkdirs();
                }
                for(int i = 0; i < indexFiles.length; i++) {
                    String name = indexFiles[i].getName();
                    indexFiles[i].renameTo(new File(totalPath + "\\" + name));
                }
                return;
            }

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
                    if (type == TypeOfTerm.City)
                        merger = new CityMerger(current, currentDir);
                    else
                        merger = new TermsMerger(current, currentDir);
                    try {
                        merger.MergeAll();
                    }catch (Exception e){
                        System.out.println("here");
                    }

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
        for(int i = 0; i < smallLetterIndexer.length; i++){
            smallLetterIndexer[i] = new TermsIndex();
            smallLetterIndexer[i].setType(TypeOfTerm.SmallLetters);
            smallLetterIndexer[i].setNumOfLetter(i);

            bigLetterIndexer[i] = new TermsIndex();
            bigLetterIndexer[i].setType(TypeOfTerm.BigLetters);
            bigLetterIndexer[i].setNumOfLetter(i);
        }

        cityIndexer = new CityIndex();
        cityIndexer.setType(TypeOfTerm.City);
        numbersIndexer = new TermsIndex();
        numbersIndexer.setType(TypeOfTerm.Number);
        rangeOrPhraseIndexer = new TermsIndex();
        rangeOrPhraseIndexer.setType(TypeOfTerm.RangeOrPhrase);
        percentageIndexer = new TermsIndex();
        percentageIndexer.setType(TypeOfTerm.Percentage);
        priceIndexer = new TermsIndex();
        priceIndexer.setType(TypeOfTerm.Price);
        dateIndexer = new TermsIndex();
        dateIndexer.setType(TypeOfTerm.Date);

        documentsDictionary = new DocumentsDictionary(destPathForTotalIndices + "\\documents");

        ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
        ExecutorService savers = Executors.newCachedThreadPool();
        //int amountOfDocs = 0;
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
            //System.out.println("waiting for task limit to be ok");
            try {
                tasksLimit.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("done waiting for task limit to be ok");

            ParserThread parserThread = new ParserThread(currentDoc);
            threadPool.submit(parserThread);

            //amountOfDocs++;

            for(int i = 0; i < smallLetterIndexer.length; i++) {
                if (checkFullIndex(threadPool, savers, smallLetterIndexer[i])) {
                    threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                    smallLetterIndexer[i] = new TermsIndex();
                    smallLetterIndexer[i].setType(TypeOfTerm.SmallLetters);
                    smallLetterIndexer[i].setNumOfLetter(i);
                    System.out.println("current file = " + currentDoc.getFilename());
                }
                if (checkFullIndex(threadPool, savers, bigLetterIndexer[i])) {
                    threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                    bigLetterIndexer[i] = new TermsIndex();
                    bigLetterIndexer[i].setType(TypeOfTerm.BigLetters);
                    bigLetterIndexer[i].setNumOfLetter(i);
                    System.out.println("current file = " + currentDoc.getFilename());
                }
            }
            if(checkFullIndex(threadPool, savers, cityIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                cityIndexer = new CityIndex();
                cityIndexer.setType(TypeOfTerm.City);
                System.out.println("current file = " + currentDoc.getFilename());
            }
            if(checkFullIndex(threadPool, savers, numbersIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                numbersIndexer = new TermsIndex();
                numbersIndexer.setType(TypeOfTerm.Number);
                System.out.println("current file = " + currentDoc.getFilename());
            }
            if(checkFullIndex(threadPool, savers, rangeOrPhraseIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                rangeOrPhraseIndexer = new TermsIndex();
                rangeOrPhraseIndexer.setType(TypeOfTerm.RangeOrPhrase);
                System.out.println("current file = " + currentDoc.getFilename());
            }
            if(checkFullIndex(threadPool, savers, percentageIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                percentageIndexer = new TermsIndex();
                percentageIndexer.setType(TypeOfTerm.Percentage);
                System.out.println("current file = " + currentDoc.getFilename());
            }
            if(checkFullIndex(threadPool, savers, priceIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                priceIndexer = new TermsIndex();
                priceIndexer.setType(TypeOfTerm.Price);
                System.out.println("current file = " + currentDoc.getFilename());
            }
            if(checkFullIndex(threadPool, savers, dateIndexer)){
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);
                dateIndexer = new TermsIndex();
                dateIndexer.setType(TypeOfTerm.Date);
                System.out.println("current file = " + currentDoc.getFilename());
            }

            /*
            if(amountOfDocs > amountOfParsedDocsInRam){
                //System.out.println("Starting to save");

                //start counting again
                //amountOfDocs = 0;

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

                ThreadIndexSaver num = new ThreadIndexSaver(numbersIndexer);
                savers.submit(num);

                ThreadIndexSaver rangePhrase = new ThreadIndexSaver(rangeOrPhraseIndexer);
                savers.submit(rangePhrase);

                ThreadIndexSaver percentage = new ThreadIndexSaver(percentageIndexer);
                savers.submit(percentage);

                ThreadIndexSaver price = new ThreadIndexSaver(priceIndexer);
                savers.submit(price);

                ThreadIndexSaver date = new ThreadIndexSaver(dateIndexer);
                savers.submit(date);

                //System.out.println("Creating new indices");
                //create the new indices
                smallLetterIndexer = new TermsIndex();
                smallLetterIndexer.setType(TypeOfTerm.SmallLetters);
                bigLetterIndexer = new TermsIndex();
                bigLetterIndexer.setType(TypeOfTerm.BigLetters);
                cityIndexer = new CityIndex();
                cityIndexer.setType(TypeOfTerm.City);
                numbersIndexer = new TermsIndex();
                numbersIndexer.setType(TypeOfTerm.Number);
                rangeOrPhraseIndexer = new TermsIndex();
                rangeOrPhraseIndexer.setType(TypeOfTerm.RangeOrPhrase);
                percentageIndexer = new TermsIndex();
                percentageIndexer.setType(TypeOfTerm.Percentage);
                priceIndexer = new TermsIndex();
                priceIndexer.setType(TypeOfTerm.Price);
                dateIndexer = new TermsIndex();
                dateIndexer.setType(TypeOfTerm.Date);

                //System.out.println("Restarting the pool");
                //restart
                threadPool = Executors.newFixedThreadPool(amountOfThreadsInThreadPool);

                System.out.println("current file = " + currentDoc.getFilename());
            }
            */
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
        for(int i = 0; i < smallLetterIndexer.length; i++) {
            ThreadIndexSaver smallLetterIndex = new ThreadIndexSaver(smallLetterIndexer[i]);
            savers.submit(smallLetterIndex);

            //System.out.println("Saving the total big letters index");
            ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer[i]);
            savers.submit(bigLetterIndex);
        }

        ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
        savers.submit(cityIndex);

        ThreadIndexSaver num = new ThreadIndexSaver(numbersIndexer);
        savers.submit(num);

        ThreadIndexSaver rangePhrase = new ThreadIndexSaver(rangeOrPhraseIndexer);
        savers.submit(rangePhrase);

        ThreadIndexSaver percentage = new ThreadIndexSaver(percentageIndexer);
        savers.submit(percentage);

        ThreadIndexSaver price = new ThreadIndexSaver(priceIndexer);
        savers.submit(price);

        ThreadIndexSaver date = new ThreadIndexSaver(dateIndexer);
        savers.submit(date);

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
        numbersIndexer = null;
        rangeOrPhraseIndexer = null;
        percentageIndexer = null;
        priceIndexer = null;
        dateIndexer = null;
    }

    private boolean checkFullIndex(ExecutorService threadPool, ExecutorService savers, AIndex indexer) {
        if(indexer.getCount() > amountOfTermsAllowedInIndex) {
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
            ThreadIndexSaver saver = new ThreadIndexSaver(indexer);
            savers.submit(saver);

            return true;
        }
        return false;
    }

    private class ParserThread implements Runnable {

        private Document document;

        public ParserThread(Document document) {
            this.document = document;
        }

        @Override
        public void run() {
            Parse parse = new Parse();
            //System.out.println("start parsing");
            List<Term> terms = parse.Parse(document, stopWords, stem);
            //System.out.println("done parsing");
            SplitAndIndex(document, terms);
            //System.out.println("done splitting");

            //tell the threadPool's controller that it can send in another thread
            tasksLimit.release();
        }
    }

    private void SplitAndIndex(Document document, List<Term> terms) {
        //Split
        //System.out.println("1");
        List<List<Term>> smallLetterTerms = new ArrayList<>(26);
        //System.out.println("2");
        List<List<Term>> bigLetterTerms = new ArrayList<>(26);
        //System.out.println("3");
        for(int i = 0; i < smallLetterIndexer.length; i++)
        {
            smallLetterTerms.add(new ArrayList<Term>());
            bigLetterTerms.add(new ArrayList<Term>());
        }
        //System.out.println("4");
        List<Term> cityTerms = new ArrayList<Term>();
        List<Term> numbersTerms = new ArrayList<Term>();
        List<Term> rangeOrPhraseTerms = new ArrayList<Term>();
        List<Term> percentageTerms = new ArrayList<Term>();
        List<Term> priceTerms = new ArrayList<Term>();
        List<Term> dateTerms = new ArrayList<Term>();
        for (Term term : terms) {
            if(term.getType() == TypeOfTerm.BigLetters) {
                char first = term.getValue().charAt(0);
                int index = first - 'A';
                bigLetterTerms.get(index).add(term);
            }
            else if(term.getType() == TypeOfTerm.City) {
                cityTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.SmallLetters) {
                char first = term.getValue().charAt(0);
                int index = first - 'a';
                smallLetterTerms.get(index).add(term);
            }
            else if(term.getType() == TypeOfTerm.Number) {
                numbersTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.RangeOrPhrase) {
                rangeOrPhraseTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.Percentage) {
                percentageTerms.add(term);
            }
            else if(term.getType() == TypeOfTerm.Price) {
                priceTerms.add(term);
            }
            else { // term.getType() == TypeOfTerm.Date
                dateTerms.add(term);
            }
        }

        Semaphore maxTfCalculatorSemaphore = new Semaphore(0, true);
        Semaphore maxTfUpdateSemaphore = new Semaphore(0, true);
        int permits = 0;
        Object lock = new Object();
        MyInteger tf = new MyInteger(0);
        MyInteger uniqueTermsNum = new MyInteger(0);
        MyInteger docIndex = new MyInteger(0);

        //System.out.println("7");
        Thread[] smallAndBigThreads = new Thread[smallLetterIndexer.length * 2];
        int smallAndBigIndex = 0;

        //System.out.println("8");
        //Ensure that there will not be a small letter and a big letter term of the same String in the same document.
        for(int i = 0; i < smallLetterTerms.size(); i++) {
            int bigSize = bigLetterTerms.get(i).size();
            for (int j = 0; j < bigSize; j++) {
                Term big = bigLetterTerms.get(i).get(j);
                boolean found = false;
                for (Term term : smallLetterTerms.get(i)) {
                    if (term.getValue().equals(big.getValue().toLowerCase()))
                        found = true;
                }
                if (found) {
                    big.setType(TypeOfTerm.SmallLetters);
                    big.setValue(big.getValue().toLowerCase());
                    smallLetterTerms.get(i).add(big);
                    bigLetterTerms.get(i).remove(j);
                    j--; //check the next big letter
                    bigSize = bigLetterTerms.get(i).size();
                }
            }

            //Index the small and big letters
            if(smallLetterTerms.get(i).size() > 0){
                IndexerThread small = new IndexerThread(smallLetterTerms.get(i), terms.size(), document.getCityInfo(), smallLetterIndexer[i], maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
                Thread thread = new Thread(small);
                thread.start();
                smallAndBigThreads[smallAndBigIndex] = thread;

                permits++;
            }
            else
                smallAndBigThreads[smallAndBigIndex] = null;

            smallAndBigIndex++;

            if(bigLetterTerms.get(i).size() > 0){
                IndexerThread big = new IndexerThread(bigLetterTerms.get(i), terms.size(), document.getCityInfo(), bigLetterIndexer[i], maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
                Thread thread = new Thread(big);
                thread.start();
                smallAndBigThreads[smallAndBigIndex] = thread;

                permits++;
            }
            else
                smallAndBigThreads[smallAndBigIndex] = null;

            smallAndBigIndex++;
        }

        //System.out.println("9");

        //Index
        Thread[] otherThreads = new Thread[6];

        //System.out.println("10");
        if(cityTerms.size() > 0){
            IndexerThread city = new IndexerThread(cityTerms, terms.size(), document.getCityInfo(), cityIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(city);
            thread.start();
            otherThreads[0] = thread;

            permits++;
        }
        else
            otherThreads[0] = null;

        //System.out.println("11");

        if(numbersTerms.size() > 0){
            IndexerThread number = new IndexerThread(numbersTerms, terms.size(), document.getCityInfo(), numbersIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(number);
            thread.start();
            otherThreads[1] = thread;

            permits++;
        }
        else
            otherThreads[1] = null;

        //System.out.println("12");
        if(rangeOrPhraseTerms.size() > 0){
            IndexerThread rangeOrPhrase = new IndexerThread(rangeOrPhraseTerms, terms.size(), document.getCityInfo(), rangeOrPhraseIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(rangeOrPhrase);
            thread.start();
            otherThreads[2] = thread;

            permits++;
        }
        else
            otherThreads[2] = null;
        //System.out.println("13");
        if(percentageTerms.size() > 0){
            IndexerThread percentage = new IndexerThread(percentageTerms, terms.size(), document.getCityInfo(), percentageIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(percentage);
            thread.start();
            otherThreads[3] = thread;

            permits++;
        }
        else
            otherThreads[3] = null;
        //System.out.println("14");
        if(priceTerms.size() > 0){
            IndexerThread price = new IndexerThread(priceTerms, terms.size(), document.getCityInfo(), priceIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(price);
            thread.start();
            otherThreads[4] = thread;

            permits++;
        }
        else
            otherThreads[4] = null;
        //System.out.println("15");
        if(dateTerms.size() > 0){
            IndexerThread date = new IndexerThread(dateTerms, terms.size(), document.getCityInfo(), dateIndexer, maxTfCalculatorSemaphore, maxTfUpdateSemaphore, lock, tf, uniqueTermsNum, docIndex);
            Thread thread = new Thread(date);
            thread.start();
            otherThreads[5] = thread;

            permits++;
        }
        else
            otherThreads[5] = null;
        //System.out.println("16");
        //wait for all of the threads to calculate their max tf:
        try {
            maxTfUpdateSemaphore.acquire(permits);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("17");

        docIndex.setValue(documentsDictionary.insert(new DocumentsDictionaryEntrance(document.getDOCNO(),document.getFilename(),uniqueTermsNum.getValue(),tf.getValue(),document.getCity())));

        //System.out.println("18");
        //tell all of the threads that they may continue, because that all of them has gotten to this point:
        maxTfCalculatorSemaphore.release(permits);

        //System.out.println("19");
        //System.out.println("waiting for threads to finish");
        for (int i = 0; i < smallAndBigThreads.length; i++){
            if(smallAndBigThreads[i] == null)
                continue;
            try {
                smallAndBigThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("20");

        for (int i = 0; i < otherThreads.length; i++){
            if(otherThreads[i] == null)
                continue;
            try {
                otherThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("21");

        //System.out.println("done waiting for threads to finish");
    }


}
