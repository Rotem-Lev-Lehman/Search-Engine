package AnalizeTools;

import Model.ADictionaryEntrance;
import Model.CityDictionaryEntrance;
import Model.TermsDictionaryEntrance;
import Model.TypeOfTerm;

import java.io.*;
import java.util.*;

public class Analizer {
    List<TermFreqTuple> termFreqTuples;
    public Analizer(){
        termFreqTuples = new LinkedList<>();
    }

    public void AnalizeAmountOfNumbers(String path){
        int count = 0;
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(path + "\\" + "dic.data")));
            while (scanner.hasNext())
            {
                String line = scanner.nextLine();
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("amount of different numbers = " + count);
    }

    public Map<String, String> AnalizeForZipf(String path){
        File mainDir = new File(path);
        File[] files = mainDir.listFiles();
        for(int i = 0; i < files.length; i++){
            if(files[i].getName().equals("documents"))
                continue;
            File[] sons = files[i].listFiles();
            TypeOfTerm type = TypeOfTerm.SmallLetters;
            if(files[i].getName().equals("bigLetters") || files[i].getName().equals("smallLetters")){
                for(int j = 0; j < sons.length; j++){
                    File[] sonsOfsons = sons[j].listFiles();
                    for(int k = 0; k < sonsOfsons.length; k++){
                        if(sonsOfsons[k].getName().equals("dic.data"))
                            readFileAndSaveTermFreqTuples(sonsOfsons[k], type);
                    }
                }
                continue;
            }
            if(files[i].getName().equals("cities"))
                type = TypeOfTerm.City;
            for(int j = 0; j < sons.length; j++){
                if(sons[j].getName().equals("dic.data"))
                    readFileAndSaveTermFreqTuples(sons[j], type);
            }
        }
        //Collections.sort(termFreqTuples);
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(TermFreqTuple tuple : termFreqTuples){
            map.put(tuple.getTerm(), String.valueOf(tuple.getFreq()));
        }
        //writeToFile();
        return map;
    }

    private void writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\User\\Desktop\\resultsOfAnalyze.csv"));
            for(TermFreqTuple tuple : termFreqTuples)
                writer.write(tuple.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileAndSaveTermFreqTuples(File dictionary, TypeOfTerm type) {
        ADictionaryEntrance dictionaryEntrance;
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(dictionary)));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if(type == TypeOfTerm.City)
                    dictionaryEntrance = CityDictionaryEntrance.ParseDictionaryRowAsMyKind(line);
                else
                    dictionaryEntrance = TermsDictionaryEntrance.ParseDictionaryRowAsMyKind(line);

                TermFreqTuple termFreqTuple = TermFreqTuple.CreateTermFreqTuple(dictionaryEntrance);
                termFreqTuples.add(termFreqTuple);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
