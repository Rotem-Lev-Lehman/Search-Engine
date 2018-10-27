package Model;

/**
 * A model class that represents the entire model of our Search Engine
 */
public class Model extends AModel {
    /**
     * An empty constructor for the model
     */
    public Model(){
        readFile = new ReadFile(new DocumentFactory());
    }
}
