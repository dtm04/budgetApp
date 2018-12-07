package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateAccountController implements Initializable, ControlledScreen{

    ScreensController myController;
    ObservableList<String> refreshList = FXCollections.observableArrayList("1 Hour", "30 minutes", "10 minutes", "5 minutes", "Never");

    @FXML
    private JFXComboBox refreshComboBox;
    @FXML
    private JFXPasswordField oldPassword;
    @FXML
    private JFXPasswordField newPassword1;
    @FXML
    private JFXPasswordField newPassword2;
    @FXML
    private JFXTextField newPrimaryCurrency;
    @FXML
    private JFXToggleButton toggleOCR;
    @FXML
    private Button passwordButton;
    @FXML
    private Button currencyButton;
    @FXML
    private Button refreshRateButton;
    @FXML
    private AnchorPane ac;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Main.currentUser != null) {
            resetFields();

        }
        refreshComboBox.setItems(refreshList);

        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);

        newPrimaryCurrency.setStyle("-fx-text-inner-color: white");
        refreshComboBox.setStyle("-fx-text-inner-color: white");
        refreshComboBox.setStyle(".label -fx-text-fill: white");
    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML
    private void goToHomePage(ActionEvent event){
        myController.unloadScreen(Main.HomePageID);
        myController.loadScreen(Main.HomePageID, Main.HomePageFile);
        myController.setScreen(Main.HomePageID);
    }

    @FXML
    private void goToCrypto(ActionEvent event){
        myController.unloadScreen(Main.CryptoCurrenciesID);
        myController.loadScreen(Main.CryptoCurrenciesID, Main.CryptoCurrenciesFile);
        myController.setScreen(Main.CryptoCurrenciesID);
    }

    @FXML
    private void goToUpdateAccount(ActionEvent event){
        myController.unloadScreen(Main.UpdateAccountID);
        myController.loadScreen(Main.UpdateAccountID, Main.UpdateAccountFile);
        myController.setScreen(Main.UpdateAccountID);
    }

    @FXML
    private void goToViewTransactions(ActionEvent event) throws IOException {
        myController.unloadScreen(Main.ViewTransactionsID);
        myController.loadScreen(Main.ViewTransactionsID, Main.ViewTransactionsFile);
        myController.setScreen(Main.ViewTransactionsID);
    }

    @FXML
    private void goToAddTransaction(ActionEvent event){
        myController.unloadScreen(Main.AddTransactionID);
        myController.loadScreen(Main.AddTransactionID, Main.AddTransactionFile);
        myController.setScreen(Main.AddTransactionID);
    }

    @FXML
    private void goToLoginPage(ActionEvent event){
        myController.unloadScreen(Main.LoginID);
        myController.loadScreen(Main.LoginID, Main.LoginFile);
        myController.setScreen(Main.LoginID);
    }

    @FXML
    private void goToAddGoalsPage(ActionEvent event){
        myController.unloadScreen(Main.AddGoalsID);
        myController.loadScreen(Main.AddGoalsID, Main.AddGoalsFile);
        myController.setScreen(Main.AddGoalsID);
    }

    @FXML
    private void changePassword(ActionEvent event){
        if (Main.currentUser.compareWithPassword(oldPassword.getText())) {
            String newPassword = newPassword1.getText();
            if (newPassword.equals(newPassword2.getText())) {
                Main.currentUser.setPassword(newPassword);
                Main.currentUser.update();

                oldPassword.setText("");
                newPassword1.setText("");
                newPassword2.setText("");
                passwordButton.setText("Changed Password");
                passwordButton.setDisable(true);

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run(){
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        passwordButton.setText("Change Password");
                                        passwordButton.setDisable(false);
                                    }
                                });
                            }
                        },
                        2000
                );
            }
        }
    }

    @FXML
    private void updatePrimaryCurrency(ActionEvent event){
        String accronym = newPrimaryCurrency.getText().trim().toUpperCase();
        int currencyId = Currency.abbrToId(accronym);
        if (currencyId != -1) {
            Main.currentUser.setPrimaryCurrency(currencyId);
            Main.currentUser.update();
            currencyButton.setText("Updated Primary Currency");
            currencyButton.setDisable(true);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run(){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    currencyButton.setText("Update Primary Currency");
                                    currencyButton.setDisable(false);
                                }
                            });
                        }
                    },
                    2000
            );
        }
    }

    @FXML
    private void changeRefreshRate(ActionEvent event){
        String refreshRate = refreshComboBox.getValue().toString();
        Main.currentUser.setRefreshRate(refreshRate);
        Main.currentUser.update();
        refreshRateButton.setText("Updated Refresh Rate");
        refreshRateButton.setDisable(true);
        int refreshRateMS = 0;
        if(refreshRate.equals("1 Hour")){
            refreshRateMS = 60 * 60 * 1000;
        } else if( refreshRate.equals("5 minutes")){
            refreshRateMS = 5 * 60 * 1000;
        } else if( refreshRate.equals("10 minutes")){
            refreshRateMS = 10 * 60 * 1000;
        } else if( refreshRate.equals(("30 minutes"))){
            refreshRateMS = 30 * 60  * 1000;
        }
        if(refreshRateMS>0){
            Main.updateTimer(refreshRateMS);
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run(){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                refreshRateButton.setText("Update Refresh Rate");
                                refreshRateButton.setDisable(false);
                            }
                        });
                    }
                },
                2000
        );
    }

    @FXML
    private void toggleOCR(ActionEvent event) {
        if (toggleOCR.isSelected()) {
            Main.currentUser.setEnableOCR(1);
            Main.currentUser.update();
        } else {
            Main.currentUser.setEnableOCR(0);
            Main.currentUser.update();
        }
    }

    private void goToPage(String pageId) {
        myController.setScreen(pageId);
        resetFields();
    }

    private void resetFields() {
        oldPassword.setText("");
        newPassword1.setText("");
        newPassword2.setText("");

        if (Main.currentUser.getOCR() == 1) {
            toggleOCR.setSelected(true);
        } else {
            toggleOCR.setSelected(false);
        }

        passwordButton.setText("Change Password");
        passwordButton.setDisable(false);

        newPrimaryCurrency.setText(Currency.idToAbbr(Main.currentUser.getPrimaryCurrency()));
        currencyButton.setText("Update Primary Currency");
        currencyButton.setDisable(false);

        refreshComboBox.setValue(Main.currentUser.getRefreshRate());
        refreshRateButton.setText("Update Refresh Rate");
        refreshRateButton.setDisable(false);
    }
}
