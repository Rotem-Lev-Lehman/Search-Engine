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
    public Model(){
        readFile = new ReadFile(new DocumentFactory());
        parser = new Parse();
    }

    @Override
    protected void startIndexing() {
        IndexerThread indexerThread = new IndexerThread();
        Thread indexer = new Thread(indexerThread);
        indexer.start();
    }

    @Override
    protected void startReadingFiles(String path) {
        ReaderThread readerThread = new ReaderThread(path);
        Thread reader = new Thread(readerThread);
        reader.start();
    }

    private class IndexerThread implements Runnable{

        @Override
        public void run() {
            index = new TermsIndex();
            while (!finishedParsing || indexQueue.size() != 0){
                MyTuple nextIndex;
                synchronized (indexLock){
                    nextIndex = indexQueue.poll();
                }
                if(nextIndex == null)
                    continue;
                index.addDocumentToIndex(nextIndex.getTerms(),nextIndex.getDocument());
            }
            finishedIndexing = true;
        }
    }

    private class ReaderThread implements Runnable{

        private String path;

        public ReaderThread(String path){
            this.path = path;
        }

        @Override
        public void run() {
            readFile.ReadFile(path, documents, lock);
            finishedRetrivingFiles = true;
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

    /** Reads a given file
     * @param file - The file that needs to be read
     * @return The file's content
     */
    private String ReadAGivenFile(File file){
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
        while (!finishedRetrivingFiles || documents.size()!=0){
            Document currentDoc;
            synchronized (lock){
                currentDoc = documents.poll();
            }
            if(currentDoc == null)
                continue;
            ParserThread parserThread = new ParserThread(currentDoc);
            threadPool.submit(parserThread);
            System.out.println("current file = " + currentDoc.getFilename());
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finishedParsing = true;
    }

    private class ParserThread implements Runnable{

        private Document document;

        public ParserThread(Document document){
            this.document = document;
        }

        @Override
        public void run() {
            Parse parse = new Parse();
            List<Term> terms = parse.Parse(document, stopWords);
            indexQueue.add(new MyTuple(document,terms));
        }
    }
}
