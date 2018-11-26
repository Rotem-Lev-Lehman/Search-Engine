package Model;

import java.time.LocalDate;

/**
 * A factory class for creating a Document from text
 */
public class DocumentFactory implements IDocumentFactory {
    @Override
    public Document CreateDocument(String doc, String filename) {
        //<DOCNO> </DOCNO>
        String DocNo = FindTextBetweenTags(doc, "DOCNO");

        //<DATE1> </DATE1>
        //String Date1 = FindTextBetweenTags(doc, "DATE1");
        //LocalDate date1 = null;
        //if(!Date1.equals(""))
        //     date1 = LocalDate.parse(Date1);

        //<TI> </TI>
        String TI = FindTextBetweenTags(doc, "TI");

        //<TEXT> </TEXT>
        String Text = FindTextBetweenTags(doc, "TEXT");

        //<F P=104> </F>
        String city = FindTextBetweenTags(doc, "<F P=104>", "</F>");

        //Create the new Document
        Document document = new Document(DocNo, null, TI, Text, filename, city);
        return document;
    }

    private String FindTextBetweenTags(String doc,String tagName) {
        try {
            return doc.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
        }
        catch (Exception e){ //change to jsoup
            //System.out.println("HERE");
        }
        return "";
    }

    private String FindTextBetweenTags(String doc,String tagBegin, String tagEnd) {
        try {
            return doc.split(tagBegin)[1].split(tagEnd)[0];
        }
        catch (Exception e){ //change to jsoup
            //System.out.println("HERE");
        }
        return "";
    }
}
