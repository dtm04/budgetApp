package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditGoalsController implements Initializable, ControlledScreen {
    ScreensController myController;
    private String date;
    //private Goal goal = AddGoalsController.currentlyEditing;

    @FXML
    JFXTextField amountField;
    @FXML
    JFXTextField currencyField;
    @FXML
    JFXDatePicker datePicker;
    @FXML
    JFXTextField currentAmountField;
    @FXML
    private AnchorPane ac;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        if(Main.currentUser != null){
            Goal goal = AddGoalsController.currentlyEditing;
            amountField.setText(String.format("%.2f", goal.getFinalGoal()));
            currencyField.setText(Currency.idToAbbr(goal.getCurrencyType()));
            currentAmountField.setText(String.format("%.2f", goal.getCurrentAmount()));
            datePicker.setValue(dateToLocalDate(goal.getGoalDate()));
        }

        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);

    }

    @FXML
    public void update(){
        Goal goal = AddGoalsController.currentlyEditing;
        try {
            goal.setFinalGoal(Double.parseDouble(amountField.getText()));
            goal.setCurrentAmount(Double.parseDouble(currentAmountField.getText()));
        } catch (NumberFormatException e) {
            System.out.println("e: amount bad");
        }

        String currencyAbbr = currencyField.getText().trim().toUpperCase();
        int currencyId = Currency.abbrToId(currencyAbbr);
        goal.setCurrencyType(currencyId);

        String newDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        goal.setGoalDate(newDate);
        checkIfComplete(goal);
        goal.update();

        myController.unloadScreen(Main.AddGoalsID);
        myController.loadScreen(Main.AddGoalsID, Main.AddGoalsFile);
        myController.setScreen(Main.AddGoalsID);

    }

    private void checkIfComplete(Goal g){
        if(g.isDone()){
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Complete!");
            a.setHeaderText("The current goal has been reached.");
            a.setContentText("Would you like to delete this goal?");

            Optional<ButtonType> result = a.showAndWait();
            if(result.get() == ButtonType.OK){
                g.remove();
            }
            else{
                a.close();
            }
        }
    }

    private LocalDate dateToLocalDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }


    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML
    private void setLabel( EventType<MouseEvent> mouseClicked){

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

}
