package Model;

import java.io.File;
import java.util.List;

/**
 * An abstract model class, all of the functionality that the model allows are in these functions
 */
public abstract class AModel {
    /**
     * The ReadFile class that can return a list of all the Documents in the path given
     */
    protected IReadFile readFile;
    protected IParse parser;
    /**
     * The Documents of the model
     */
    protected List<Document> documents;

    /** Creates all of the Documents in the given path
     * @param path - The path where all of the Documents are in
     */
    public void GetAllDocuments(String path){
        documents = readFile.ReadFile(path,parser);
        System.out.println(documents.size());
    }

    public void SetStopWords(File file){
        parser.CreateStopWords(file);
    }

    /** Gets the Document at the given index
     * @param index - The index of the wanted Document
     * @return The Document at the given index
     */
    public Document GetDocumentAt(int index){
        return documents.get(index);
    }
}
