package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    protected void startReadingFiles(String path) {
        ReaderThread readerThread = new ReaderThread(path);
        Thread reader = new Thread(readerThread);
        reader.start();
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
        while (!finishedRetrivingFiles){
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
    }

    private class ParserThread implements Runnable{

        private Document document;

        public ParserThread(Document document){
            this.document = document;
        }

        @Override
        public void run() {
            Parse parse = new Parse();
            parse.Parse(document, stopWords);
        }
    }
}
