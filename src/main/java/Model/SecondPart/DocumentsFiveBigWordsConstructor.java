package Model.SecondPart;

import Model.DocumentsDictionaryController;
import Model.EntranceRow;
import Model.TermsIndexFileController;

import java.io.File;
import java.util.ArrayList;

public class DocumentsFiveBigWordsConstructor {
    private TermsIndexFileController currentFileController;
    private DocumentsDictionaryController documentsDictionaryController;
    private File[] files;

    public DocumentsFiveBigWordsConstructor(File documentsDir, File[] bigLettersDir){
        documentsDictionaryController = new DocumentsDictionaryController(documentsDir);
        files = bigLettersDir;
    }

    public void construct(){
        documentsDictionaryController.ReadAllDictionary();
        for(int i = 0; i < files.length; i++) {
            currentFileController = new TermsIndexFileController();
            currentFileController.OpenFile(files[i].getAbsolutePath());

            while (!currentFileController.done()){
                currentFileController.getNextRow();
                ArrayList<EntranceRow> row = currentFileController.getPostingRow().getEntranceRows();
                for (EntranceRow entrance : row) {
                    documentsDictionaryController.insertIfBetter(currentFileController.getTerm(), entrance, currentFileController.getDocFreq());
                }
            }
        }
        documentsDictionaryController.WriteTheDictionaryToDisk();
    }
}
