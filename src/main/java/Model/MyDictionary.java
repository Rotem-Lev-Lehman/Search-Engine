package Model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MyDictionary {
    private Map<String, ADictionaryEntrance> map;


    /**
     * Constructor for a dictionary , (use of TreeMap<String, ADictionaryEntrance)
     */
    public MyDictionary(){
        map = new LinkedHashMap<String, ADictionaryEntrance>();
        //map = new TreeMap<String, ADictionaryEntrance>(); // sorted :)
    }

    /**
     * @param dictionaryEntrance
     * add entrance to the map (dictionary)
     */
    public void addEntrance(ADictionaryEntrance dictionaryEntrance){
        map.put(dictionaryEntrance.getTerm(),dictionaryEntrance);
    }


    /**
     * @param term
     * @return true if the dictionary contains term or false if not
     */
    public boolean exist(Term term){
        return map.containsKey(term.getValue());
    }

    /**
     * @param term
     * @return Dictionary entrance for a specific term
     */
    public ADictionaryEntrance getEntrance(Term term){
        return map.get(term.getValue());
    }

    /**
     * @return return the dictionary, (Map<String, ADictionaryEntrance>)
     */
    public Map<String, ADictionaryEntrance> getMap() {
        return map;
    }

    /**
     * Setter for the Dictionary
     */
    public void setMap(Map<String, ADictionaryEntrance> map) {
        this.map = map;
    }

    /**
     * @return true if map(Dictionary) is empty or false if not.
     */
    public boolean isEmpty(){
        return map.isEmpty();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ADictionaryEntrance> tuple : map.entrySet()) {
            builder.append(tuple.getValue().toString());
        }
        return builder.toString();
    }
}
