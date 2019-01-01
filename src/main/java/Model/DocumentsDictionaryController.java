package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DocumentsDictionaryController {
    private File dictionaryFile;
    private ArrayList<DocumentsDictionaryEntrance> dictionary;
    private int N; //num of documents
    private double avgDocLength; //avgdl for the BM25 formula
    public DocumentsDictionaryController(File directory){
        this.dictionaryFile = new File(directory.getAbsoluteFile() + "\\dic.data");
    }

    public void ReadAllDictionary(boolean usePreviousData) {
        dictionary = new ArrayList<DocumentsDictionaryEntrance>();
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(dictionaryFile)));
            avgDocLength = 0;
            while (scanner.hasNext()) {
                DocumentsDictionaryEntrance curr = DocumentsDictionaryEntrance.Parse(scanner.nextLine(), usePreviousData);
                dictionary.add(curr);
                avgDocLength += (((double)(curr.getUniqueWordsAmount() * curr.getMaxTf())) / 2.0);
            }

            scanner.close();
            N = dictionary.size();
            avgDocLength = (avgDocLength / (double)N);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void WriteTheDictionaryToDisk(){
        //first delete the existing file, so we will be able to replace it.
        if(dictionaryFile.delete()){
            //now write the new dictionary file
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFile));
                for(DocumentsDictionaryEntrance entrance : dictionary){
                    writer.write(entrance.toString());
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                throw new Exception("wasn't able to delete the documents dictionary file in order to replace it!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public DocumentsDictionaryEntrance getDictionaryEntrance(int documentID){
        return dictionary.get(documentID);
    }

    public int getN(){
        return N;
    }

    public double getAvgDocLength(){
        return avgDocLength;
    }

    public void addToCossimCalculation(EntranceRow entrance, int df){
        int index = entrance.getDocId();
        DocumentsDictionaryEntrance dictionaryEntrance = dictionary.get(index);

        double score = calculateScore(entrance.getNormalizedTermFreq(), df);
        synchronized (dictionaryEntrance.lockSumOfLeftSide) {
            dictionaryEntrance.AddToSumOfCossim(score);
        }
    }

    public void insertIfBetterAndAddToCossimCalculation(String term, EntranceRow entrance, int df){
        int index = entrance.getDocId();
        DocumentsDictionaryEntrance dictionaryEntrance = dictionary.get(index);

        double score = calculateScore(entrance.getNormalizedTermFreq(), df);
        synchronized (dictionaryEntrance.lockSumOfLeftSide) {
            dictionaryEntrance.AddToSumOfCossim(score);
        }

        IdentityAndScore[] topFive = dictionaryEntrance.getTopFiveBigWords();
        for(int i = 0; i < topFive.length; i++) {
            try {
                topFive[i].lock.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (topFive[i] == null) {
                topFive[i].setTerm(term);
                topFive[i].setScore(score);

                topFive[i].lock.release();
                return;
            }
            if(topFive[i].getScore() < score){
                //move it all one to the side and drop the least significant one
                for(int j = i + 1; j < topFive.length; j++) {
                    try {
                        topFive[j].lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for(int j = topFive.length - 1; j > i; j--) {
                    topFive[j].setTerm(topFive[j - 1].getTerm());
                    topFive[j].setScore(topFive[j - 1].getScore());

                    topFive[j].lock.release();
                }
                //save the new one
                topFive[i].setTerm(term);
                topFive[i].setScore(score);

                topFive[i].lock.release();
                return;
            }
            topFive[i].lock.release();
        }
    }

    private double calculateScore(double tf, int df){
        //calculate idf:
        double idf = Math.log(((double)N)/((double)df)) / Math.log(2);
        //calculate tf * idf
        return tf * idf;

    }
}
