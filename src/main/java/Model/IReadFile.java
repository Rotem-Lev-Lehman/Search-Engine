package Model;

import java.util.List;

/**
 * An interface for reading the files and getting all of the Documents in them
 */
public interface IReadFile {
    /**
     * @param path - The path of the root directory of all of the files
     * @return A list of all the Documents in the files
     */
    List<Document> ReadFile(String path, IParse parse);
}
