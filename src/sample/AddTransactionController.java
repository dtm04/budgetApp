package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class AddTransactionController implements Initializable, ControlledScreen{

    ScreensController myController;

    private Payment newPayment;
    private Income newIncome;

    @FXML
    private JFXTextField imagePathField;
    @FXML
    private Button ocrButton;
    @FXML
    private ImageView receiptImage;

    @FXML
    private JFXTextField amountField;
    @FXML
    private JFXTextField currencyField;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXTextField otherPartyField;
    @FXML
    private RadioButton payment;
    @FXML
    private RadioButton income;
    @FXML
    private AnchorPane ac;
    @FXML
    private AnchorPane ac2;

    @FXML
    private Label errorLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);

        AnchorPane.setTopAnchor(ac2, 0.0);
        AnchorPane.setLeftAnchor(ac2, 0.0);
        AnchorPane.setRightAnchor(ac2, 0.0);
        AnchorPane.setBottomAnchor(ac2, 0.0);

        if (Main.currentUser != null && Main.currentUser.getOCR() == 0) {
            imagePathField.setVisible(false);
            ocrButton.setVisible(false);
            receiptImage.setVisible(false);
        }

        errorLabel.setVisible(false);
        imagePathField.setStyle("-fx-text-inner-color: white");
        datePicker.setStyle("-fx-text-inner-color: white");
        currencyField.setStyle("-fx-text-inner-color: white");
        amountField.setStyle("-fx-text-inner-color: white");
        imagePathField.setStyle("-fx-text-inner-color: white");
        otherPartyField.setStyle("-fx-text-inner-color: white");

        datePicker.setValue(LocalDate.ofEpochDay(System.currentTimeMillis() / 86400000)); // millisecond to day

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
    private void manualLog(ActionEvent event) {
        errorLabel.setVisible(false);
        boolean missingAmount = false, missingDate = false;
        double amount = -1;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0.0) {
                missingAmount = true;
            }
        } catch (NumberFormatException invalidAmountE) {
            missingAmount = true;
        }
        String currencyAbbr = currencyField.getText().trim().toUpperCase();
        int currencyId = Currency.abbrToId(currencyAbbr);
        if (currencyId == -1) {
            // Default to user's primary default currency
            currencyId = Main.currentUser.getPrimaryCurrency();
        } else {
            currencyId = currencyId;
        }
        long epoch;
        try {
            LocalDate date = datePicker.getValue();
            ZoneId zoneId = ZoneId.systemDefault();
            epoch = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        } catch (Exception e) {
            epoch = -1;
            missingDate = true;
        }

        String otherParty = otherPartyField.getText();
        boolean typePayment = payment.isSelected();
        boolean typeIncome = income.isSelected();

        if (missingAmount || missingDate) {
            if (missingAmount && !missingDate) {
                errorLabel.setText("Invalid amount");
            } else if (!missingAmount && missingDate) {
                errorLabel.setText("Invalid date");
            } else {
                errorLabel.setText("Invalid amount AND invalid date");
            }
            errorLabel.setVisible(true);
        } else {
            if (typePayment) {
                if (newPayment == null) {
                    newPayment = new Payment();
                }
                newPayment.setAmount(amount);
                newPayment.setCurrencyType(currencyId);
                newPayment.setDate(epoch);
                newPayment.setOtherParty(otherParty);
                newPayment.setUserId(Main.currentUser.getUserId());

                newPayment.create();
                myController.unloadScreen(Main.ViewTransactionsID);
                myController.loadScreen(Main.ViewTransactionsID, Main.ViewTransactionsFile);
                myController.setScreen(Main.ViewTransactionsID);
            } else if (typeIncome) {
                if (newIncome == null) {
                    newIncome = new Income();
                }
                newIncome.setAmount(amount);
                newIncome.setCurrencyType(currencyId);
                newIncome.setDate(epoch);
                newIncome.setOtherParty(otherParty);
                newIncome.setUserId(Main.currentUser.getUserId());

                newIncome.create();
                myController.unloadScreen(Main.ViewTransactionsID);
                myController.loadScreen(Main.ViewTransactionsID, Main.ViewTransactionsFile);
                myController.setScreen(Main.ViewTransactionsID);
            }
        }
    }

    @FXML
    private void useOCR(ActionEvent event) {
        String imagePath = imagePathField.getText();
        receiptImage.setImage(new Image(new File(imagePath).toURI().toString()));
        amountField.setText(OCR.getTotalReceiptPrice(imagePath));
        imagePathField.setText("");
    }

    private void goToScreen(String screenId) {
        myController.setScreen(screenId);
        resetFields();
    }

    private void resetFields() {
        newPayment = new Payment();
        newIncome = new Income();
        imagePathField.setText("");
        amountField.setText("");
        currencyField.setText("");
        payment.setSelected(true);
        otherPartyField.setText("");
    }

}
