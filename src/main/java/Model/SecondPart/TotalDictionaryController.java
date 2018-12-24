package Model.SecondPart;

import Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TotalDictionaryController {
    private Map<TypeOfTerm, Map<String, ADictionaryEntrance>> totalDictionary;
    private Map<TypeOfTerm, List<Map<String, ADictionaryEntrance>>> lettersDictionary;
    private Map<TypeOfTerm, File> postingFiles;
    private Map<TypeOfTerm, File[]> lettersPostingFiles;
    private DocumentsDictionaryController documentsDictionaryController;

    public TotalDictionaryController(File indicesFolder){

        totalDictionary = new EnumMap<>(TypeOfTerm.class);
        lettersDictionary = new EnumMap<>(TypeOfTerm.class);
        postingFiles = new EnumMap<>(TypeOfTerm.class);
        lettersPostingFiles = new EnumMap<>(TypeOfTerm.class);

        for(TypeOfTerm type : TypeOfTerm.values()){
            if(type == TypeOfTerm.SmallLetters || type == TypeOfTerm.BigLetters){
                lettersDictionary.put(type, new ArrayList<>(26));
                lettersPostingFiles.put(type, new File[26]);
            }
            else
                totalDictionary.put(type, new HashMap<>());
        }

        String totalFolder = indicesFolder.getAbsolutePath();

        String smallFolder = totalFolder + "\\smallLetters";
        String bigFolder = totalFolder + "\\bigLetters";
        String cityFolder = totalFolder + "\\cities";
        String numFolder = totalFolder + "\\numbers";
        String rangeOrPhraseFolder = totalFolder + "\\rangeOrPhrase";
        String percentageFolder = totalFolder + "\\percentage";
        String priceFolder = totalFolder + "\\price";
        String dateFolder = totalFolder + "\\date";

        String documentsFolder = totalFolder + "\\documents";

        String[] smallLetters = new String[26];
        String[] bigLetters = new String[26];
        for(int i = 0; i < smallLetters.length; i++){
            char currLetter = (char)('a' + i);
            smallLetters[i] = smallFolder + "\\" + currLetter;
            bigLetters[i] = bigFolder + "\\" + currLetter;

            List<Map<String, ADictionaryEntrance>> smallDictionary = lettersDictionary.get(TypeOfTerm.SmallLetters);
            smallDictionary.add(new HashMap<>());
            createDictionary(smallDictionary.get(i), smallLetters[i], TypeOfTerm.SmallLetters, i);

            List<Map<String, ADictionaryEntrance>> bigDictionary = lettersDictionary.get(TypeOfTerm.BigLetters);
            bigDictionary.add(new HashMap<>());
            createDictionary(bigDictionary.get(i), bigLetters[i], TypeOfTerm.BigLetters, i);
        }

        Map<String, ADictionaryEntrance> city = totalDictionary.get(TypeOfTerm.City);
        createDictionary(city, cityFolder, TypeOfTerm.City, -1);
        Map<String, ADictionaryEntrance> num = totalDictionary.get(TypeOfTerm.Number);
        createDictionary(num, numFolder, TypeOfTerm.Number, -1);
        Map<String, ADictionaryEntrance> rangeOrPhrase = totalDictionary.get(TypeOfTerm.RangeOrPhrase);
        createDictionary(rangeOrPhrase, rangeOrPhraseFolder, TypeOfTerm.RangeOrPhrase, -1);
        Map<String, ADictionaryEntrance> percentage = totalDictionary.get(TypeOfTerm.Percentage);
        createDictionary(percentage, percentageFolder, TypeOfTerm.Percentage, -1);
        Map<String, ADictionaryEntrance> price = totalDictionary.get(TypeOfTerm.Price);
        createDictionary(price, priceFolder, TypeOfTerm.Price, -1);
        Map<String, ADictionaryEntrance> date = totalDictionary.get(TypeOfTerm.Date);
        createDictionary(date, dateFolder, TypeOfTerm.Date, -1);

        documentsDictionaryController = new DocumentsDictionaryController(new File(documentsFolder));
        documentsDictionaryController.ReadAllDictionary(true);


    }

    private void createDictionary(Map<String, ADictionaryEntrance> dictionary, String folderPath, TypeOfTerm type, int index){
        String postingFile = folderPath + "\\post.data";
        String dictionaryFile = folderPath + "\\dic.data";
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(dictionaryFile)));

            while (scanner.hasNext()) {
                ADictionaryEntrance entrance;
                if(type == TypeOfTerm.City)
                    entrance = CityDictionaryEntrance.ParseDictionaryRowAsMyKind(scanner.nextLine());
                else
                    entrance = TermsDictionaryEntrance.ParseDictionaryRowAsMyKind(scanner.nextLine());

                dictionary.put(entrance.getTerm(),entrance);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(type == TypeOfTerm.SmallLetters || type == TypeOfTerm.BigLetters){
            lettersPostingFiles.get(type)[index] = new File(postingFile);
        }
        else{
            postingFiles.put(type, new File(postingFile));
        }
    }

    public Map<String, ADictionaryEntrance> getDictionary(TypeOfTerm type){
        return totalDictionary.get(type);
    }

    public Map<String, ADictionaryEntrance> getDictionaryFromLetters(TypeOfTerm type, int firstLetter){
        return lettersDictionary.get(type).get(firstLetter);
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

    public int getN(){
        return documentsDictionaryController.getN();
    }

    public double getAvgDocLength(){
        return documentsDictionaryController.getAvgDocLength();
    }
}
