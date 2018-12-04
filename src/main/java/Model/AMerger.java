package Model;

import java.util.ArrayList;
import java.util.List;

public abstract class AMerger {
    protected AIndexFileController[] fileControllers;
    protected List<Integer> notFinishedFiles;
    protected String[] filesToMerge;
    protected String saveToFile;
    protected ACompleteIndexFileController completeIndexFileController;

    public AMerger(String[] filesToMerge, String saveToFile) {
        this.filesToMerge = filesToMerge;
        this.saveToFile = saveToFile;
        notFinishedFiles = new ArrayList<>();
        fileControllers = new AIndexFileController[filesToMerge.length];
        initializeFileControllers();
        for(int i = 0; i < filesToMerge.length; i++){
            notFinishedFiles.add(i);
            fileControllers[i].OpenFile(filesToMerge[i]);
        }
        initializeCompleteIndexFileController();
        completeIndexFileController.OpenFile(saveToFile);
    }

    protected abstract void initializeFileControllers();

    protected abstract void initializeCompleteIndexFileController();

    public void MergeAll(){
        while (!notFinishedFiles.isEmpty()){
            if(notFinishedFiles.size() > 1) {
                List<Integer> minIndices = findCurrentMin();

                int currentIndex = minIndices.get(0);

                PostingRow mergedPostingRow = fileControllers[currentIndex].getPostingRow();
                int mergedDocFreq = fileControllers[currentIndex].getDocFreq();
                ADictionaryEntrance mergedDictionaryEntrance = CreateNewDictionaryEntrance(fileControllers[currentIndex].getDictionaryEntrance());

                for (int i = 1; i < minIndices.size(); i++) {
                    currentIndex = minIndices.get(i);
                    mergedPostingRow.merge(fileControllers[currentIndex].getPostingRow());
                    mergedDocFreq += fileControllers[currentIndex].getDocFreq();
                }

                mergedDictionaryEntrance.setDocFreq(mergedDocFreq);

                completeIndexFileController.SaveRow(mergedDictionaryEntrance, mergedPostingRow);
                for (int i : minIndices) {
                    fileControllers[i].getNextRow();
                    if (fileControllers[i].done())
                        notFinishedFiles.remove((Integer) i);
                }
            }
            else{ // must be size == 1
                completeIndexFileController.SaveRest(fileControllers[notFinishedFiles.get(0)]);
            }
        }
        //done merging
    }

    protected abstract ADictionaryEntrance CreateNewDictionaryEntrance(ADictionaryEntrance dictionaryEntrance);

    private List<Integer> findCurrentMin() {
        List<Integer> min = new ArrayList<>();
        min.add(notFinishedFiles.get(0));
        String minTerm = fileControllers[notFinishedFiles.get(0)].getTerm();
        for(int i = 1; i < notFinishedFiles.size(); i++){
            int index = notFinishedFiles.get(i);
            String term = fileControllers[index].getTerm();
            int compare = term.compareTo(minTerm);
            if(compare < 0){
                //new term is the new minimum
                minTerm = term;
                min = new ArrayList<>();
                min.add(index);
            }
            else if(compare == 0){
                // The same as the minimum
                min.add(index);
            }
            //if compare > 0 nothing to do, just ignore this case...
        }
        return min;
    }
}
