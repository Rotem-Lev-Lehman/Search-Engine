package View;

import Control.AController;

public abstract class AView {
    protected AController controller;

    public AController getController() {
        return controller;
    }

    public void setController(AController controller) {
        this.controller = controller;
    }
}
