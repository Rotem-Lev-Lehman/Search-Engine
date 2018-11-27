package Model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private Map<String, ADictionaryEntrance> map;

    public Dictionary(){
        map = new ConcurrentHashMap<String, ADictionaryEntrance>();
    }

    public void addEntrance(ADictionaryEntrance dictionaryEntrance){
        map.put(dictionaryEntrance.getTerm(),dictionaryEntrance);
    }

    public boolean exist(Term term){
        return map.containsKey(term.getValue());
    }
    public ADictionaryEntrance getEntrance(Term term){
        return map.get(term.getValue());
    }
}
