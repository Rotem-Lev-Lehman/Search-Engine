package Model.SecondPart;

import Model.DocumentsDictionaryController;
import Model.EntranceRow;
import Model.TermsIndexFileController;

import java.io.File;
import java.util.ArrayList;

public class DocumentsFiveBigWordsAndCossimConstructor {
    private DocumentsDictionaryController documentsDictionaryController;
    private File[] files;

    public DocumentsFiveBigWordsAndCossimConstructor(DocumentsDictionaryController documentsDictionaryController, File[] bigLettersDir){
        this.documentsDictionaryController = documentsDictionaryController;
        files = bigLettersDir;
    }

    public void construct(){
        Thread[] threads = new Thread[files.length];
        for(int i = 0; i < files.length; i++) {
            threads[i] = new Thread(new constructingThread(files[i]));
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class constructingThread implements Runnable{

        private File file;
        public constructingThread(File file){
            this.file = file;
        }
        @Override
        public void run() {
            TermsIndexFileController currentFileController = new TermsIndexFileController();
            currentFileController.OpenFile(file.getAbsolutePath());

            while (!currentFileController.done()){
                currentFileController.getNextRow();
                ArrayList<EntranceRow> row = currentFileController.getPostingRow().getEntranceRows();
                for (EntranceRow entrance : row) {
                    documentsDictionaryController.insertIfBetterAndAddToCossimCalculation(currentFileController.getTerm(), entrance, currentFileController.getDocFreq());
                }
            }
        }
    }
}
