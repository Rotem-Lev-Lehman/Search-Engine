package Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DocumentsDictionary {
    private int currentIndex;
    private Object lock;
    private BufferedWriter writer;

    public DocumentsDictionary(String path) {
        lock = new Object();
        currentIndex = 0;

        try {
            writer = new BufferedWriter(new FileWriter(path + "\\dic.data"));
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

    public int insert(DocumentsDictionaryEntrance documentsDictionaryEntrance){
        int i;
        synchronized (lock) {
            try {
                writer.write(documentsDictionaryEntrance.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            i = currentIndex;
            currentIndex++;
        }
        return i;
    }
}
