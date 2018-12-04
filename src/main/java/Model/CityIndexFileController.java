package Model;

public class CityIndexFileController extends AIndexFileController {
    @Override
    protected void parseDictionaryRow(String dictionaryRow) {
        currentDictionaryEntrance = CityDictionaryEntrance.ParseDictionaryRowAsMyKind(dictionaryRow);
    }
}
