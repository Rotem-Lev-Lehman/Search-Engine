package View;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ShowResultsView extends AView implements Initializable {
    public TableView<QueryResultForView> tableViewResults;

    public TableColumn<QueryResultForView,String> queryId;
    public TableColumn<QueryResultForView,Integer> rank;
    public TableColumn<QueryResultForView,String> documentId;

    public TableColumn<QueryResultForView,String> e1;
    public TableColumn<QueryResultForView,Double> s1;
    public TableColumn<QueryResultForView,String> e2;
    public TableColumn<QueryResultForView,Double> s2;
    public TableColumn<QueryResultForView,String> e3;
    public TableColumn<QueryResultForView,Double> s3;
    public TableColumn<QueryResultForView,String> e4;
    public TableColumn<QueryResultForView,Double> s4;
    public TableColumn<QueryResultForView,String> e5;
    public TableColumn<QueryResultForView,Double> s5;

    public CheckBox checkBoxShowFiveEntities;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        queryId.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("queryID")
        );
        rank.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Integer>("rank")
        );
        documentId.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("documentID")
        );

        e1.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("e1")
        );
        s1.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Double>("s1")
        );

        e2.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("e2")
        );
        s2.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Double>("s2")
        );

        e3.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("e3")
        );
        s3.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Double>("s3")
        );

        e4.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("e4")
        );
        s4.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Double>("s4")
        );

        e5.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, String>("e5")
        );
        s5.setCellValueFactory(
                new PropertyValueFactory<QueryResultForView, Double>("s5")
        );


        checkBoxShowFiveEntities.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                setVisibleForEntities(newValue);
            }
        });

        //remove entities
        checkBoxShowFiveEntities.setSelected(false);
        setVisibleForEntities(false);
    }

    public void setResults(List<QueryResultForView> results){
        ObservableList<QueryResultForView> observableList = new ObservableListWrapper<QueryResultForView>(results);
        tableViewResults.setItems(observableList);
    }

    private void setVisibleForEntities(boolean newValue){
        e1.setVisible(newValue);
        s1.setVisible(newValue);

        e2.setVisible(newValue);
        s2.setVisible(newValue);

        e3.setVisible(newValue);
        s3.setVisible(newValue);

        e4.setVisible(newValue);
        s4.setVisible(newValue);

        e5.setVisible(newValue);
        s5.setVisible(newValue);
    }

}
