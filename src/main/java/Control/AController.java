package Control;

import Model.AModel;
import Model.SecondPart.SecondPartModel;

import java.util.Observer;

/**
 * An abstract class representing the controller of the application. Controls the transmission between the View and the Model
 */
public abstract class AController implements Observer {
    /**
     * The model of the application
     */
    protected AModel model;

    /**
     * The model for the second part of the application
     */
    protected SecondPartModel secondPartModel;

    /** A getter for the model
     * @return The model of the application
     */
    public AModel getModel() {
        return model;
    }

    /** A setter for the model
     * @param model - The model of the application
     */
    public void setModel(AModel model) {
        this.model = model;
    }

    /** A getter for the second part model
     * @return The second part model of the application
     */
    public SecondPartModel getSecondPartModel() {
        return secondPartModel;
    }

    /** A setter for the second part model
     * @param secondPartModel - The second part model of the application
     */
    public void setSecondPartModel(SecondPartModel secondPartModel) {
        this.secondPartModel = secondPartModel;
    }
}
