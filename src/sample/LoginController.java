package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.application.Application.launch;

public class LoginController implements Initializable, ControlledScreen{

    ScreensController myController;
    @FXML
    AnchorPane ac;
    @FXML
    AnchorPane ac2;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton signIn;
    @FXML
    private JFXButton createButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label createLabel;

    private String pass;
    private String user;
    private User tempUser;

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
    private void validateLogin(ActionEvent event){
        pass = password.getText();
        user = username.getText();
        loadUserFromDatabase();
        if(tempUser != null) {
            if (tempUser.compareWithPassword(pass)) {
                Main.currentUser = tempUser;
                myController.unloadScreen(Main.HomePageID);
                myController.loadScreen(Main.HomePageID, Main.HomePageFile);
                myController.setScreen(Main.HomePageID);
                username.setText("");
                password.setText("");

                String currentRefreshRate = Main.currentUser.getRefreshRate();
                int refreshRateMS = 0;
                if (currentRefreshRate.equals("1 Hour")) {
                    refreshRateMS = 60 * 60 * 1000;
                } else if (currentRefreshRate.equals("5 minutes")) {
                    refreshRateMS = 5 * 60 * 1000;
                } else if (currentRefreshRate.equals("10 minutes")) {
                    refreshRateMS = 10 * 60 * 1000;
                } else if (currentRefreshRate.equals(("30 minutes"))) {
                    refreshRateMS = 30 * 60 * 1000;
                }
                if (refreshRateMS > 0) {
                    Main.setTimer(refreshRateMS);
                }
            }
        }
    }

    @FXML
    private void goToCreateAccount(ActionEvent event){
        myController.setScreen(Main.CreateAccountID);
    }


    private void loadUserFromDatabase(){
        tempUser = User.getUser(user);
    }

}
