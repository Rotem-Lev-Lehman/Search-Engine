package Model;

import java.util.HashMap;
import java.util.Map;

public class MyDictionary {
    private Map<String, ADictionaryEntrance> map;

    public MyDictionary(){
        map = new HashMap<String, ADictionaryEntrance>();
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
