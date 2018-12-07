package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountController implements Initializable, ControlledScreen{

    private ScreensController myController;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private AnchorPane ac;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField confirmationField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;

    private User cUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);
    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML
    private void attemptCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmation = confirmationField.getText();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        boolean valid, match, unique;
        match = password.equals(confirmation);
        valid = (!password.equals("") && !username.equals(""));
        unique = (User.getUser(username) == null);

        if (valid && match && unique) {
            cUser = new User();
            cUser.setUserName(username);
            // TODO Salted password
            cUser.setPassword(password);
            cUser.setFirstName(firstName);
            cUser.setLastName(lastName);
            cUser.create();
            usernameField.setText("");
            passwordField.setText("");
            confirmationField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            // Have to do a lookup to ensure that the user id is not set to 0.
            Main.currentUser = User.getUser(username);
            myController.unloadScreen(Main.HomePageID);
            myController.loadScreen(Main.HomePageID, Main.HomePageFile);
            myController.setScreen(Main.HomePageID);
        }
    }

    @FXML
    private void goToLogin(ActionEvent event){
        myController.setScreen(Main.LoginID);
    }

}
