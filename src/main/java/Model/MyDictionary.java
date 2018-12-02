package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MyDictionary {
    private Map<String, ADictionaryEntrance> map;

    public MyDictionary(){
        map = new HashMap<String, ADictionaryEntrance>();
        //map = new TreeMap<String, ADictionaryEntrance>(); // sorted :)
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

    public Map<String, ADictionaryEntrance> getMap() {
        return map;
    }

    public void setMap(Map<String, ADictionaryEntrance> map) {
        this.map = map;
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }
}
