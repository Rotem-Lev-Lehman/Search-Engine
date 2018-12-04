package Model;

public class TermsIndexFileController extends AIndexFileController {
    @Override
    protected void parseDictionaryRow(String dictionaryRow) {
        currentDictionaryEntrance = TermsDictionaryEntrance.ParseDictionaryRowAsMyKind(dictionaryRow);
    }
}
