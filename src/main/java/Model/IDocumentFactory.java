package Model;

/**
 * An interface for creating a Document from text
 */
public interface IDocumentFactory {
    /**
     * @param doc - the document before parsing
     * @return a parsed Document
     */
    Document CreateDocument(String doc, String filename);
}
