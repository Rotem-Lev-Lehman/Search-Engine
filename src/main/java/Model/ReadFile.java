package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A concrete class for getting a list of all the Documents in the files
 */
public class ReadFile implements IReadFile {
    /**
     * The DocumentFactory we will use
     */
    private IDocumentFactory documentFactory;

    /**
     * The files in the given folder
     */
    private List<File> files;

    /** A constructor for the ReadFile class
     * @param documentFactory - The DocumentFactory we will use
     */
    public ReadFile(IDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    /**
     * @param path      - The path of the root directory of all of the files
     * @param documents
     * @param lock
     * Read the file from the path and add all the documents from it to documents list.
     */
    @Override
    public void ReadFile(String path, Queue<Document> documents, Object lock, Semaphore empty, Semaphore full) {
        final File folder = new File(path);
        files = new ArrayList<File>();
        listFilesForFolder(folder);

        for (File file : files) {
            //System.out.println("current file = " + file.getName() + ", ");
            List<Document> docs = GetAllDocuments(file);
            //int bias = 700;
            for (int i = 0; i < docs.size(); i++) {
                //boolean wait = false;
                try {
                    full.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock){
                    //if(documents.size() > bias)
                        //wait = true;
                    //else
                    documents.add(docs.get(i));
                }
                empty.release();
                /*
                if(wait) {
                    try {
                        i--;
                        Thread.sleep(70);
                        bias /= 2;
                        if(bias < 100)
                            bias = 700;
                        continue;
                        //Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                bias = 700;
                */
            }
        }
    }

    /** Gets all of the Documents from a given file
     * @param file - The given file
     * @return The list of the Documents in the file
     */
    private List<Document> GetAllDocuments(File file) {
        List<Document> documents = new ArrayList<Document>();
        String content = ReadAGivenFile(file);
        String[] docs = content.split("<DOC>");
        for (int i = 1; i < docs.length; i++) {
            docs[i] = "<DOC>" + docs[i];
            documents.add(documentFactory.CreateDocument(docs[i], file.getName()));
        }
        return documents;
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

    /** Creates the list of the files in the given folder
     * @param folder - The folder to search files in
     */
    private void listFilesForFolder(final File folder) {
        if(folder != null) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    files.add(fileEntry);
                }
            }
        }
    }
}
