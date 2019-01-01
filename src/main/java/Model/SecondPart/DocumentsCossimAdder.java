package Model.SecondPart;

import Model.*;

import java.io.File;
import java.util.ArrayList;

public class DocumentsCossimAdder {
    private DocumentsDictionaryController documentsDictionaryController;
    private String file;
    private TypeOfTerm type;

    public DocumentsCossimAdder(DocumentsDictionaryController controller, String file, TypeOfTerm type){
        documentsDictionaryController = controller;
        this.file = file;
        this.type = type;
    }

    public void AddForAll(){
        AIndexFileController currentFileController;
        if(type == TypeOfTerm.City)
            currentFileController = new CityIndexFileController();
        else
            currentFileController = new TermsIndexFileController();

        currentFileController.OpenFile(file);

        while (!currentFileController.done()){
            currentFileController.getNextRow();
            ArrayList<EntranceRow> row = currentFileController.getPostingRow().getEntranceRows();
            for (EntranceRow entrance : row) {
                documentsDictionaryController.addToCossimCalculation(entrance, currentFileController.getDocFreq());
            }
        }
    }
}
