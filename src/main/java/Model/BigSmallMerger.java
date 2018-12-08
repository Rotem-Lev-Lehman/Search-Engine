package Model;

public class BigSmallMerger {
    protected AIndexFileController bigLettersFileController;
    protected AIndexFileController smallLettersFileController;
    protected ACompleteIndexFileController completeBigLettersIndexFileController;
    protected ACompleteIndexFileController completeSmallLettersIndexFileController;

    public BigSmallMerger(String startBigLettersDirectory, String startSmallLettersDirectory, String completeBigLettersDirectory, String completeSmallLettersDirectory){
        bigLettersFileController = new TermsIndexFileController();
        smallLettersFileController = new TermsIndexFileController();

        completeBigLettersIndexFileController = new TermsCompleteIndexFileController();
        completeSmallLettersIndexFileController = new TermsCompleteIndexFileController();

        bigLettersFileController.OpenFile(startBigLettersDirectory);
        smallLettersFileController.OpenFile(startSmallLettersDirectory);

        completeBigLettersIndexFileController.OpenFile(completeBigLettersDirectory);
        completeSmallLettersIndexFileController.OpenFile(completeSmallLettersDirectory);
    }

    public void MergeAll(){
        bigLettersFileController.getNextRow();
        smallLettersFileController.getNextRow();
        while (true){
            if(bigLettersFileController.done() || smallLettersFileController.done()) {
                FinishAll();
                return;
            }

            String big = bigLettersFileController.getTerm().toLowerCase();
            String small = smallLettersFileController.getTerm();
            int compare = small.compareTo(big);
            if(compare < 0) {
                completeSmallLettersIndexFileController.SaveRow(smallLettersFileController.getDictionaryEntrance(), smallLettersFileController.getPostingRow());

                smallLettersFileController.getNextRow();
            }
            else if(compare > 0){
                completeBigLettersIndexFileController.SaveRow(bigLettersFileController.getDictionaryEntrance(), bigLettersFileController.getPostingRow());

                bigLettersFileController.getNextRow();
            }
            else{ //equal...
                PostingRow mergedPostingRow = smallLettersFileController.getPostingRow();
                mergedPostingRow.merge(bigLettersFileController.getPostingRow()); // no problem with that because that we have ensured there is no document that both of the terms were in together

                ADictionaryEntrance smallDictionary = smallLettersFileController.getDictionaryEntrance();
                ADictionaryEntrance bigDictionary = bigLettersFileController.getDictionaryEntrance();

                ADictionaryEntrance mergedDictionaryEntrance = new TermsDictionaryEntrance(small,smallDictionary.getDocFreq() + bigDictionary.getDocFreq(), smallDictionary.getTotalTermFreq() + bigDictionary.getTotalTermFreq(), 0);

                completeSmallLettersIndexFileController.SaveRow(mergedDictionaryEntrance, mergedPostingRow);

                smallLettersFileController.getNextRow();
                bigLettersFileController.getNextRow();
            }
        }
    }

    private void FinishAll(){
        if(!bigLettersFileController.done())
            completeBigLettersIndexFileController.SaveRest(bigLettersFileController);
        if(!smallLettersFileController.done())
            completeSmallLettersIndexFileController.SaveRest(smallLettersFileController);

        completeBigLettersIndexFileController.CloseFile();
        completeSmallLettersIndexFileController.CloseFile();
    }
}
