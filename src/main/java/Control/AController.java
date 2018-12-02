package Control;

import Model.AModel;
import Model.AModel2;

import java.util.Observer;

/**
 * An abstract class representing the controller of the application. Controls the transmission between the View and the Model
 */
public abstract class AController implements Observer {
    /**
     * The model of the application
     */
    protected AModel2 model;

    /** A getter for the model
     * @return The model of the application
     */
    public AModel2 getModel() {
        return model;
    }

    /** A setter for the model
     * @param model - The model of the application
     */
    public void setModel(AModel2 model) {
        this.model = model;
    }
}
