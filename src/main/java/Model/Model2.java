package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Model2 extends AModel2 {

    public Model2(){
        readFile = new ReadFile(new DocumentFactory());

        smallLetterIndexer = new TermsIndex();
        smallLetterIndexer.setType(TypeOfIndex.SmallLetters);
        bigLetterIndexer = new TermsIndex();
        bigLetterIndexer.setType(TypeOfIndex.BigLetters);
        cityIndexer = new CityIndex();
        cityIndexer.setType(TypeOfIndex.City);
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
                System.out.println("current file = " + tuple.getFilename());
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
            readFile.ReadFile(path, documents, lock);
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
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-2);
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

            amountOfDocs++;

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
                ThreadIndexSaver smallLetterIndex = new ThreadIndexSaver(smallLetterIndexer);
                Thread indexSaver = new Thread(smallLetterIndex);
                indexSaver.start();

                ThreadIndexSaver bigLetterIndex = new ThreadIndexSaver(bigLetterIndexer);
                Thread bigSaver = new Thread(bigLetterIndex);
                bigSaver.start();

                /*
                ThreadIndexSaver cityIndex = new ThreadIndexSaver(cityIndexer);
                Thread citySaver = new Thread(cityIndex);
                citySaver.start();
                */

                //create the new indices
                smallLetterIndexer = new TermsIndex();
                bigLetterIndexer = new TermsIndex();
                cityIndexer = new CityIndex();

                //restart
                threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            }

            System.out.println("current file = " + currentDoc.getFilename());
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
        if(smallLetterTerms.size() > 0)
            smallLetterIndexer.addDocumentToIndex(smallLetterTerms,document.getDOCNO(),document.getFilename());
        if(bigLetterTerms.size() > 0)
            bigLetterIndexer.addDocumentToIndex(bigLetterTerms,document.getDOCNO(),document.getFilename());
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
