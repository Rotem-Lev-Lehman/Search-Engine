package Model.SecondPart;

import Model.ADictionaryEntrance;
import Model.DocumentsDictionaryController;
import Model.DocumentsDictionaryEntrance;
import Model.TypeOfTerm;

import java.io.File;
import java.util.Map;

public class TotalDictionaryController {
    Map<TypeOfTerm, Map<String, ADictionaryEntrance>> totalDictionary;
    Map<TypeOfTerm, Map<String, ADictionaryEntrance>[]> lettersDictionary;
    Map<TypeOfTerm, File> postingFiles;
    Map<TypeOfTerm, File[]> lettersPostingFiles;
    private DocumentsDictionaryController documentsDictionaryController;

    public TotalDictionaryController(File indicesFolder){
        get all of the files for each type, and save it to its dictionary
    }

    public Map<String, ADictionaryEntrance> getDictionary(TypeOfTerm type){
        return totalDictionary.get(type);
    }

    public Map<String, ADictionaryEntrance> getDictionaryFromLetters(TypeOfTerm type, int firstLetter){
        return lettersDictionary.get(type)[firstLetter];
    }

    public File getPosting(TypeOfTerm type){
        return postingFiles.get(type);
    }

    public File getPostingFromLetters(TypeOfTerm type, int firstLetter){
        return lettersPostingFiles.get(type)[firstLetter];
    }

    public DocumentsDictionaryEntrance getDocumentsDictionaryEntrance(int docID){
        return documentsDictionaryController.getDictionaryEntrance(docID);
    }
}
