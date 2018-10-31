package Model;

import java.time.LocalDate;

/**
 * A factory class for creating a Document from text
 */
public class DocumentFactory implements IDocumentFactory {
    @Override
    public Document CreateDocument(String doc) {
        //<DOCNO> </DOCNO>
        String DocNo = FindTextBetweenTags(doc, "DOCNO");

        //<DATE1> </DATE1>
        String Date1 = FindTextBetweenTags(doc, "DATE1");
        LocalDate date1 = LocalDate.parse(Date1);

        //<TI> </TI>
        String TI = FindTextBetweenTags(doc, "TI");

        //<TEXT> </TEXT>
        String Text = FindTextBetweenTags(doc, "TEXT");

        //Create the new Document
        Document document = new Document(DocNo, date1, TI, Text);
        return document;
    }

    private String FindTextBetweenTags(String doc,String tagName) {
        return doc.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
    }
}
