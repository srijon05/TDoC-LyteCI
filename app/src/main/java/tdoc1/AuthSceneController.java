package tdoc1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuthSceneController {

    @FXML
    private Label fullnameLabel;

    @FXML
    private TextField fullnameTF;

    @FXML
    private Label heading;

    @FXML
    private Button saveButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTF;

    String username = new String();
    String fullname = new String();

    @FXML
    void saveButtonAction(ActionEvent event) {
        username = usernameTF.getText().trim();
        fullname = fullnameTF.getText().trim();

        if (fullname.isEmpty()) {
            System.out.println("username cannot be empty");
        } else {
            heading.setText("Welcome " + fullname);
        }

    }

}
