package Model;

public class CityMerger extends AMerger {

    public CityMerger(String[] filesToMerge, String saveToFile) {
        super(filesToMerge, saveToFile);
    }

    @Override
    protected void initializeFileControllers() {
        for(int i = 0; i < fileControllers.length; i++)
            fileControllers[i] = new CityIndexFileController();
    }

    @Override
    protected void initializeCompleteIndexFileController() {
        completeIndexFileController = new CityCompleteIndexFileController();
    }

    @Override
    protected ADictionaryEntrance CreateNewDictionaryEntrance(ADictionaryEntrance dictionaryEntrance) {
        CityDictionaryEntrance entrance = (CityDictionaryEntrance)dictionaryEntrance;
        return new CityDictionaryEntrance(entrance.getTerm(),entrance.getCurrency(),entrance.getPopulationSize(),0,0);
    }
}
