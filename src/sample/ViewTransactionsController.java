package sample;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ViewTransactionsController implements Initializable, ControlledScreen{

    ScreensController myController;
    ObservableList<String> categoryList = FXCollections.observableArrayList( "Amount Greater Than", "Amount Less Than", "Date After", "Date Before", "Currency","Other Party");

    public static Transaction currentlyEditting;

    @FXML
    private TextField queryString;
    @FXML
    private JFXComboBox categoryComboBox;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<?, ?> col1;
    @FXML
    private TableColumn<?, ?> col2;
    @FXML
    private TableColumn<?, ?> col3;
    @FXML
    private TableColumn<?, ?> col4;
    @FXML
    private TableColumn<?, ?> col5;
    @FXML
    private TableColumn<Transaction, String> edit;
    @FXML
    private TableColumn<Transaction, String> del;
    @FXML
    private RadioButton payment;
    @FXML
    private RadioButton income;
    @FXML
    private RadioButton both;
    @FXML
    private AnchorPane ac;

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private ObservableList<Transaction> payData;
    private Payment[] allPayments;
    private Income[] allIncome;

    public ViewTransactionsController getController(){
        return this;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Main.currentUser!=null) {
            allIncome = Income.getAllIncome();
            allPayments = Payment.getAllPayments();
            categoryComboBox.setItems(categoryList);
            categoryComboBox.setValue(categoryList.get(0));
            displayItems();
        }
        queryString.setPromptText("");
        categoryComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue.equals("Date After")){
                    queryString.setPromptText("yyyy-mm-dd");
                } else if(newValue.equals("Date Before")){
                    queryString.setPromptText("yyyy-mm-dd");
                } else if(newValue.equals("Amount Greater Than")){
                    queryString.setPromptText("abbr value");
                }else if(newValue.equals("Amount Less Than")){
                    queryString.setPromptText("abbr value");
                }else if(newValue.equals("Currency")){
                    queryString.setPromptText("abbr");
                }else{
                    queryString.setPromptText("");
                }
            }
        });
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);
    }

    private void setCells(){
        col1.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        col2.setCellValueFactory(new PropertyValueFactory<>("amount"));
        col3.setCellValueFactory(new PropertyValueFactory<>("currencyAbbreviation"));
        col4.setCellValueFactory(new PropertyValueFactory<>("otherParty"));
        col5.setCellValueFactory(new PropertyValueFactory<>("dateString"));

        Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> delCellFactory =
                new Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Transaction, String> param) {
                        final TableCell<Transaction, String> cell = new TableCell<Transaction, String>() {
                            final Button btn = new Button("Delete");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Transaction transaction = getTableView().getItems().get(getIndex());
                                        if (transaction.getTransactionType().equals("+")) {
                                            Income in = (Income) transaction;
                                            in.remove();
                                        } else {
                                            Payment pm = (Payment) transaction;
                                            pm.remove();
                                        }
                                        myController.unloadScreen(Main.ViewTransactionsID);
                                        myController.loadScreen(Main.ViewTransactionsID, Main.ViewTransactionsFile);
                                        myController.setScreen(Main.ViewTransactionsID);
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

        Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> editCellFactory =
                new Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Transaction, String> param) {
                        final TableCell<Transaction, String> cell = new TableCell<Transaction, String>() {
                            final Button btn = new Button("Edit");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        currentlyEditting = getTableView().getItems().get(getIndex());
                                        myController.unloadScreen(Main.EditTransactionID);
                                        myController.loadScreen(Main.EditTransactionID, Main.EditTransactionFile);
                                        myController.setScreen(Main.EditTransactionID);
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

    @FXML
    public void displayItems() {
        categoryComboBox.setItems(categoryList);
        categoryComboBox.setValue(categoryList.get(5));
        payData = FXCollections.observableArrayList();
        setCells();
        //TODO: use transaction/payment/income sql methods
        loadPaymentData();
        loadIncomeData();
        transactionTable.setItems(payData);
    }

    public void search(){
        payData.clear();
        boolean transactionTypePayment = payment.isSelected();
        boolean transactionTypeIncome = income.isSelected();
        boolean transactionTypeBoth = both.isSelected();

        String searchTransaction = categoryComboBox.getValue().toString();
        String inputString = queryString.getText().trim();

        if(searchTransaction.equals("Amount Greater Than")){
            if(transactionTypePayment){
                getPaymentDataOverAmount(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataOverAmount(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataOverAmount(inputString);
                getIncomeDataOverAmount(inputString);
            }

        }else if(searchTransaction.equals("Amount Less Than")){
            if(transactionTypePayment){
                getPaymentDataUnderAmount(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataUnderAmount(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataUnderAmount(inputString);
                getIncomeDataUnderAmount(inputString);
            }
        }else if(searchTransaction.equals("Date After")){
            if(transactionTypePayment){
                getPaymentDataAfterDate(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataAfterDate(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataAfterDate(inputString);
                getIncomeDataAfterDate(inputString);
            }
        }else if(searchTransaction.equals("Date Before")){
            if(transactionTypePayment){
                getPaymentDataBeforeDate(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataBeforeDate(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataBeforeDate(inputString);
                getIncomeDataBeforeDate(inputString);
            }
        }else if(searchTransaction.equals("Currency")){
            if(transactionTypePayment){
                getPaymentDataWithCurrency(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataWithCurrency(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataWithCurrency(inputString);
                getIncomeDataWithCurrency(inputString);
            }
        }else if(searchTransaction.equals("Other Party")){
            if(transactionTypePayment){
                getPaymentDataWithOtherParty(inputString);
            }else if(transactionTypeIncome){
                getIncomeDataWithOtherParty(inputString);
            }else if(transactionTypeBoth){
                getPaymentDataWithOtherParty(inputString);
                getIncomeDataWithOtherParty(inputString);
            }
        }
        transactionTable.setItems(payData);
    }


    public void getPaymentDataAfterDate(String dateInMS){
        long stringDateToLong = Main.dateToLong(dateInMS);
        Payment[] payments = new Payment[0];
        if(stringDateToLong != 0){
            payments = allPayments;
        }

        for (Payment p : payments) {
            p.setTransactionType("-");
            if(p.date/1000>stringDateToLong){
                payData.add(p);
            }
        }
    }

    public void getPaymentDataBeforeDate(String dateInMS){
        long stringDateToLong = Main.dateToLong(dateInMS);
        Payment[] payments = new Payment[0];
        if(stringDateToLong != 0){
            payments = allPayments;
        }
        for (Payment p : payments) {
            if(p.date/1000<stringDateToLong){
                payData.add(p);
            }
        }
    }

    public void getPaymentDataOverAmount(String currencyAmount){
        if(!currencyAmount.equals("")){
            Payment[] payments = allPayments;
            String[] inputStrings = currencyAmount.split(" ");
            if(inputStrings.length == 2){
                int currencyID = Currency.abbrToId(inputStrings[0].toUpperCase());

                try{
                    double inputAmount = Double.parseDouble(inputStrings[1]);
                    if(!(currencyID == -1 || inputAmount<0)) {
                        for (Payment p : payments) {
                            p.setTransactionType("-");
                            if (p.currencyType == currencyID && p.amount > inputAmount) {
                                payData.add(p);
                            }
                        }
                    }
                } catch (Exception e){

                }
            }
        }
    }

    public void getPaymentDataUnderAmount(String currencyAmount){
        if(!currencyAmount.equals("")){
            Payment[] payments = allPayments;
            String[] inputStrings = currencyAmount.split(" ");
            if(inputStrings.length == 2){
                int currencyID = Currency.abbrToId(inputStrings[0].toUpperCase());

                try{
                    double inputAmount = Double.parseDouble(inputStrings[1]);
                    if(!(currencyID == -1 || inputAmount<0)) {
                        for (Payment p : payments) {
                            p.setTransactionType("-");
                            if (p.currencyType == currencyID && p.amount < inputAmount) {
                                payData.add(p);
                            }
                        }
                    }
                } catch (Exception e){

                }
            }
        }
    }

    public void getPaymentDataWithCurrency(String currency){
        Payment[] payments = allPayments;
        int currencyID = Currency.abbrToId(currency.toUpperCase());
        if(!(currencyID == -1)) {
            for (Payment p : payments) {
                p.setTransactionType("-");
                if (p.currencyType == currencyID) {
                    payData.add(p);
                }
            }
        }
    }

    public void getPaymentDataWithOtherParty(String otherParty){
        Payment[] payments = allPayments;
        for (Payment p : payments) {
            p.setTransactionType("-");
            if (p.otherParty.matches(".*"+otherParty+".*")) {
                payData.add(p);
            }
        }
    }

    public void getIncomeDataAfterDate(String dateInMS){
        long stringDateToLong = Main.dateToLong(dateInMS);
        Income[] income = new Income[0];
        if(stringDateToLong != 0){
            income = allIncome;
        }
        for (Income i : income) {
            i.setTransactionType("+");
            if(i.date/1000>stringDateToLong){
                payData.add(i);
            }
        }
    }

    public void getIncomeDataOverAmount(String currencyAmount){
        if(!currencyAmount.equals("")){
            Income[] income = allIncome;
            String[] inputStrings = currencyAmount.split(" ");
            if(inputStrings.length == 2){
                int currencyID = Currency.abbrToId(inputStrings[0].toUpperCase());

                try{
                    double inputAmount = Double.parseDouble(inputStrings[1]);
                    if(!(currencyID == -1 || inputAmount<0)) {
                        for (Income i : income) {
                            i.setTransactionType("+");
                            if (i.currencyType == currencyID && i.amount > inputAmount) {
                                payData.add(i);
                            }
                        }
                    }
                } catch (Exception e){

                }
            }
        }
    }

    public void getIncomeDataUnderAmount(String currencyAmount){
        if(!currencyAmount.equals("")){
            Income[] income = allIncome;
            String[] inputStrings = currencyAmount.split(" ");
            if(inputStrings.length == 2){
                int currencyID = Currency.abbrToId(inputStrings[0].toUpperCase());

                try{
                    double inputAmount = Double.parseDouble(inputStrings[1]);
                    if(!(currencyID == -1 || inputAmount<0)) {
                        for (Income i : income) {
                            i.setTransactionType("+");
                            if (i.currencyType == currencyID && i.amount < inputAmount) {
                                payData.add(i);
                            }
                        }
                    }
                } catch (Exception e){

                }
            }
        }
    }
    public void getIncomeDataBeforeDate(String dateInMS){
        long stringDateToLong = Main.dateToLong(dateInMS);
        Income[] income = new Income[0];
        if(stringDateToLong != 0){
            income = allIncome;
        }
        for (Income i : income) {
            i.setTransactionType("+");
            if(i.date/1000<stringDateToLong){
                payData.add(i);
            }
        }
    }

    public void getIncomeDataWithCurrency(String currency){
        Income[] income = allIncome;
        int currencyID = Currency.abbrToId(currency.toUpperCase());
        if(!(currencyID == -1)) {
            for (Income i : income) {
                i.setTransactionType("+");
                if (i.currencyType == currencyID) {
                    payData.add(i);
                }
            }
        }
    }

    public void getIncomeDataWithOtherParty(String otherParty){
        Income[] income = allIncome;
        for (Income i : income) {
            i.setTransactionType("+");
            if (i.otherParty.matches(".*"+otherParty+".*")) {
                payData.add(i);
            }
        }
    }
    private void loadPaymentData(){
        Payment[] payments = allPayments;
        for (Payment p : payments) {
            p.setTransactionType("-");
            payData.add(p);
        }
    }

    private void loadIncomeData(){
        Income[] income = allIncome;
        for (Income i : income) {
            i.setTransactionType("+");
            payData.add(i);
        }
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
