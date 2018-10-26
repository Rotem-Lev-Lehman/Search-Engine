package Control;

import Model.AModel;

public abstract class AController {
    protected AModel model;

    public AModel getModel() {
        return model;
    }

    public void setModel(AModel model) {
        this.model = model;
    }
}
