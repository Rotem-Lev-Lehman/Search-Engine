package Model;

import java.util.List;

/**
 * An interface for parsing a document
 */
public interface IParse {
    /** Parses the document to a list of all of it's terms
     * @param document - The document to parse
     * @return A list of all the terms
     */
    List<Term> Parse(Document document);
}
