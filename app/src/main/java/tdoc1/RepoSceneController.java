package tdoc1;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.stage.Stage;

public class RepoSceneController {

    @FXML
    private Button back;

    @FXML
    private Label heading;

    @FXML
    private Button next;

    @FXML
    private ListView<String> repoListView;

    @FXML
    private Label welcomeLabel;

    public void initialize(){
        welcomeLabel.setText("Welcome " + Globals.fullName + "!");
        populateRepoList(Globals.repoNames);
    }

    public void populateRepoList(ObservableList<String> repoNames){
        repoListView.setItems(repoNames);
        repoListView.setCellFactory(ComboBoxListCell.forListView(repoNames));
        repoListView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String old_val, String new_val ){
                Globals.selectedRepoName = new_val;
            }
        });
    }

    @FXML
    void backButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AuthScene"));
            Parent prevSceneRoot = loader.load();
            Stage mainWindow = (Stage) heading.getScene().getWindow();
            AuthSceneController controller = loader.getController();
            Scene prevScene  = new Scene(prevSceneRoot);
            mainWindow.setScene(prevScene);
            mainWindow.setTitle("AuthScene");
            mainWindow.show();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @FXML
    void nextButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuildScene.fxml"));
            Parent nextSceneRoot = loader.load();
            Stage mainWindow = (Stage) heading.getScene().getWindow();
            BuildSceneController controller = loader.getController();
            controller.initializerepo();
            Scene nextScene = new Scene(nextSceneRoot);
            mainWindow.setScene(nextScene);
            mainWindow.setTitle("Clone-Build");
            mainWindow.show();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
