package View;

import Control.AController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;

/**
 * An abstract class representing the View of the application
 */
public abstract class AView extends Observable {
    /**
     * The controller of the application
     */
    protected AController controller;

    /**
     * The current stage
     */
    protected Stage currStage;

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
        this.addObserver(controller);
    }

    public Stage getCurrStage() {
        return currStage;
    }

    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }

    public void ShowSuccess(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setContentText(text);
        alert.showAndWait();
    }

    public void ShowFailure(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setContentText(text);
        alert.showAndWait();
    }

    protected AView ChangeView(String viewName, String title, int width, int height){
        AView next = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("/ShowResultsPage.fxml").openStream());

            currStage.setTitle(title);
            currStage.setScene(new Scene(root, width, height));
            currStage.show();

            next = (AView) fxmlLoader.getController();
            next.setController(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return next;
    }
}
