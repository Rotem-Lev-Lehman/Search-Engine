package Model;

public interface IDictionary {
    void addEntrance(ADictionaryEntrance dictionaryEntrance);
    boolean exist(Term term);
    ADictionaryEntrance getEntrance(Term term);
}
