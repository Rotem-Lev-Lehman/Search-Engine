package Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentsDictionary {
    private List<DocumentsDictionaryEntrance> documents;
    private int currentIndex;
    private Object lock;
    private BufferedWriter writer;

    public DocumentsDictionary(String path) {
        documents = new ArrayList<DocumentsDictionaryEntrance>();
        lock = new Object();
        currentIndex = 0;
        try {
            writer = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DocumentsDictionaryEntrance get(int i){
        return documents.get(i);
    }

    public int insert(DocumentsDictionaryEntrance documentsDictionaryEntrance){
        int i;
        synchronized (lock) {
            documents.add(documentsDictionaryEntrance);
            i = currentIndex;
            currentIndex++;
        }
        return i;
    }
}
