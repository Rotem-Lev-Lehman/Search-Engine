package View;

import Control.AController;

/**
 * An abstract class representing the View of the application
 */
public abstract class AView {
    /**
     * The controller of the application
     */
    protected AController controller;

    /** A getter for the controller of the application
     * @return The controller of the application
     */
    public AController getController() {
        return controller;
    }

    /** A setter for the controller of the application
     * @param controller - The controller of the application
     */
    public void setController(AController controller) {
        this.controller = controller;
    }
}
