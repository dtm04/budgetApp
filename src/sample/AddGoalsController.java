package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddGoalsController implements Initializable, ControlledScreen{

    ScreensController myController;
    public static Goal currentlyEditing;
    private Goal newGoal;
    private Goal[] allGoals;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private ObservableList<Goal> data;

    @FXML
    private Button addButton;
    @FXML
    private TableView<Goal> goalsTable;
    @FXML
    private TableColumn<?, ?> dateCol;
    @FXML
    private TableColumn<?, ?> currencyCol;
    @FXML
    private TableColumn<?, ?> finalCol;
    @FXML
    private TableColumn<?, ?> currentCol;
    @FXML
    private TableColumn<?, ?> nameCol;
    @FXML
    private TableColumn<?, ?> descriptionCol;
    @FXML
    private JFXButton logGoalButton;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField amountField;
    @FXML
    private JFXTextField descriptionField;
    @FXML
    private JFXTextField currencyTypeField;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private TableColumn<Goal, String> edit;
    @FXML
    private TableColumn<Goal, String> del;
    @FXML
    private AnchorPane ac;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Main.currentUser!=null){
            data = FXCollections.observableArrayList();
            getAllGoals();
            setCells();
            //clearTextBoxes();
        }
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);
    }

    @FXML
    public void handleAddButton(ActionEvent e) throws SQLException {
        //TODO: real dates, and ability to select goals from table to update isdone and current amount
        //data.clear();
        //getAllGoals();
        int currentUserId;
        double amount = Double.valueOf(amountField.getText());
        String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String name = nameField.getText();
        String description = descriptionField.getText();
        if(Main.currentUser != null){
            currentUserId = Main.currentUser.getUserId();
        } else {
            currentUserId = 0001;
        }

        if(newGoal == null){
            newGoal = new Goal();
        }
        String currencyAbbr = currencyTypeField.getText().trim().toUpperCase();
        int currencyId = Currency.abbrToId(currencyAbbr);
        if (currencyId == -1) {
            // Default to user's primary default currency
            currencyId = Main.currentUser.getPrimaryCurrency();
        } else {
            currencyId = currencyId;
        }
        newGoal.setUserId(currentUserId);
        newGoal.setGoalName(name);
        newGoal.setFinalGoal(amount);
        newGoal.setGoalDate(date);
        newGoal.setGoalDescription(description);
        newGoal.setCurrencyType(currencyId);
        //newGoal.setDone(false);
        //newGoal.setCurrentAmount(0);
        newGoal.create();
        getAllGoals();
        //data.add(newGoal);
        clearTextBoxes();
        //setCells();

    }

    private void getAllGoals(){
        allGoals = Goal.getAllGoals();
        data.clear();
        for(Goal g : allGoals){
            data.add(g);
        }
        goalsTable.setItems(data);
    }

    private void setCells(){
        dateCol.setCellValueFactory(new PropertyValueFactory<>("goalDate"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currencyAbbreviation"));
        finalCol.setCellValueFactory(new PropertyValueFactory<>("finalGoal"));
        currentCol.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("goalName"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("goalDescription"));

        //delete buttons
        Callback<TableColumn<Goal, String>, TableCell<Goal, String>> delCellFactory =
                new Callback<TableColumn<Goal, String>, TableCell<Goal, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Goal, String> param) {
                        final TableCell<Goal, String> cell = new TableCell<Goal, String>() {
                            final Button btn = new Button("Delete");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Goal g = getTableView().getItems().get(getIndex());
                                        g.remove();
                                        myController.unloadScreen(Main.AddGoalsID);
                                        myController.loadScreen(Main.AddGoalsID, Main.AddGoalsFile);
                                        myController.setScreen(Main.AddGoalsID);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        del.setCellFactory(delCellFactory);

        //edit buttons
        Callback<TableColumn<Goal, String>, TableCell<Goal, String>> editCellFactory =
                new Callback<TableColumn<Goal, String>, TableCell<Goal, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Goal, String> param) {
                        final TableCell<Goal, String> cell = new TableCell<Goal, String>() {
                            final Button btn = new Button("Edit");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        currentlyEditing = getTableView().getItems().get(getIndex());
                                        myController.unloadScreen(Main.EditGoalsID);
                                        myController.loadScreen(Main.EditGoalsID, Main.EditGoalsFile);
                                        myController.setScreen(Main.EditGoalsID);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        edit.setCellFactory(editCellFactory);
    }
    private void clearTextBoxes(){
        if(datePicker.getValue() != null){
            datePicker.setValue(null);
        }
        nameField.clear();
        amountField.clear();
        descriptionField.clear();
        currencyTypeField.clear();
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
}
