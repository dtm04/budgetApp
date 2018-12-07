package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;

import javax.print.DocFlavor;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class EditTransactionController implements Initializable, ControlledScreen {
    ScreensController myController;

    @FXML
    private AnchorPane ac;
    @FXML
    private JFXTextField amountField;
    @FXML
    private JFXTextField currencyField;
    @FXML
    private JFXTextField otherPartyField;
    @FXML
    private AnchorPane ac2;
    @FXML
    private DatePicker datePicker;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Main.currentUser != null) {
            Transaction transaction = ViewTransactionsController.currentlyEditting;
            amountField.setText(String.format("%.2f", transaction.getAmount()));
            currencyField.setText(Currency.idToAbbr(transaction.getCurrencyType()));
            otherPartyField.setText(transaction.getOtherParty());
            datePicker.setValue(LocalDate.ofEpochDay(transaction.getDate() / 86400000)); // millisecond to day
        }
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);

        AnchorPane.setTopAnchor(ac2, 0.0);
        AnchorPane.setLeftAnchor(ac2, 0.0);
        AnchorPane.setRightAnchor(ac2, 0.0);
        AnchorPane.setBottomAnchor(ac2, 0.0);

        datePicker.setStyle("-fx-text-inner-color: white");
        currencyField.setStyle("-fx-text-inner-color: white");
        amountField.setStyle("-fx-text-inner-color: white");
        otherPartyField.setStyle("-fx-text-inner-color: white");
    }

    @FXML
    public void update() {
        Transaction t = ViewTransactionsController.currentlyEditting;
        try {
            t.setAmount(Double.parseDouble(amountField.getText()));
        } catch (NumberFormatException invalidAmountE) {
            // Do nothing
        }

        String currencyAbbr = currencyField.getText().trim().toUpperCase();
        int currencyId = Currency.abbrToId(currencyAbbr);
        if (currencyId != -1) {
            t.setCurrencyType(currencyId);
        }

        long epoch;
        try {
            LocalDate date = datePicker.getValue();
            ZoneId zoneId = ZoneId.systemDefault();
            epoch = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
            t.setDate(epoch);
        } catch (Exception e) {
            // Do nothing
        }

        t.setOtherParty(otherPartyField.getText());

        if (t.getTransactionType().equals("+")) {
            ((Income) t).update();
        } else {
            ((Payment) t).update();
        }

        myController.unloadScreen(Main.ViewTransactionsID);
        myController.loadScreen(Main.ViewTransactionsID, Main.ViewTransactionsFile);
        myController.setScreen(Main.ViewTransactionsID);
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
    private void goToEditGoalsPage(ActionEvent event){
        myController.unloadScreen(Main.EditGoalsID);
        myController.loadScreen(Main.EditGoalsID, Main.EditGoalsFile);
        myController.setScreen(Main.EditGoalsID);
    }

    @FXML
    private void goToAddGoalsPage(ActionEvent event){
        myController.unloadScreen(Main.AddGoalsID);
        myController.loadScreen(Main.AddGoalsID, Main.AddGoalsFile);
        myController.setScreen(Main.AddGoalsID);
    }

}
