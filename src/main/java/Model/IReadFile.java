package Model;

import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * An interface for reading the files and getting all of the Documents in them
 */
public interface IReadFile {
    /**
     * @param path - The path of the root directory of all of the files
     * @return A list of all the Documents in the files
     */
    void ReadFile(String path, Queue<Document> documents, Object lock, Semaphore empty, Semaphore full);
}
