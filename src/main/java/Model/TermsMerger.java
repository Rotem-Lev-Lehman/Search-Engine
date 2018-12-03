package Model;

public class TermsMerger extends AMerger {

    public TermsMerger(String[] filesToMerge, String saveToFile) {
        super(filesToMerge, saveToFile);
    }

    @Override
    protected void initializeFileControllers() {
        for (int i = 0; i < fileControllers.length; i++)
            fileControllers[i] = new TermsIndexFileController();
    }

    @Override
    protected void initializeCompleteIndexFileController() {
        completeIndexFileController = new TermsCompleteIndexFileController();
    }

    @Override
    protected ADictionaryEntrance CreateNewDictionaryEntrance(ADictionaryEntrance dictionaryEntrance) {
        return new TermsDictionaryEntrance(dictionaryEntrance.getTerm(),0,0);
    }
}
