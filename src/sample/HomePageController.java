package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HomePageController implements Initializable, ControlledScreen{

    ScreensController myController;
    private ObservableList<Goal> data;
    private Goal[] allGoals;
    private PreparedStatement ps;
    private ResultSet rs;
    private int index =0;
    private int goalIndex=0;
    private double maxProgress;
    int[] userCurrencies;
    double[] currencyValues;
    double[] amounts;
    double progress;
    double amount;
    double usdAmount;
    private Goal displayGoal;
    private double primAmount;
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    @FXML
    Label name;
    @FXML
    Label accountBalance;
    @FXML
    AnchorPane ac;
    @FXML
    Label gn_label;
    @FXML
    Label ed_label;
    @FXML
    Label ga_label;
    @FXML
    Label as_label;
    @FXML
    Label gn_head;
    @FXML
    Label ed_head;
    @FXML
    Label ga_head;
    @FXML
    Label as_head;
    @FXML
    ProgressBar g_progress;
    @FXML
    PieChart cur_piechart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Main.currentUser!=null) {
            setName();
            setAccountBalance();
            setAmountLabels();
            getHighestProgressGoal();

            if (displayGoal == null) {
                as_head.setVisible(false);
                ga_head.setVisible(false);
                ed_head.setVisible(false);
                gn_head.setVisible(false);
                g_progress.setVisible(false);
                ga_label.setVisible(false);
                ed_label.setVisible(false);
                gn_label.setVisible(false);
                as_label.setVisible(false);
            }
        }
        AnchorPane.setTopAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setBottomAnchor(ac, 0.0);
    }

    private void getHighestProgressGoal(){
        allGoals = Goal.getAllGoals();
        if (allGoals != null && allGoals.length > 0) {
            displayGoal = allGoals[0];
            for (int i = 1; i <= allGoals.length - 1; i++) {
                if (displayGoal.getGoalProgress() <= allGoals[i].getGoalProgress()) {
                    displayGoal = allGoals[i];
                }
            }

            if (displayGoal != null) {
                gn_label.setText(displayGoal.getGoalName());
                ed_label.setText(displayGoal.getGoalDate());
                ga_label.setText(String.format("%.2f", displayGoal.getFinalGoal()));
                as_label.setText(String.format("%.2f", displayGoal.getCurrentAmount()));
                g_progress.setProgress(displayGoal.getGoalProgress());
            }
        }
    }

    private void setName(){
        if (!Main.currentUser.getFirstName().isEmpty()) {
            if (!Main.currentUser.getLastName().isEmpty()) {
                name.setText(Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName());
            } else {
                name.setText(Main.currentUser.getFirstName());
            }
        } else {
            name.setText(Main.currentUser.getUserName());
        }
     }

     private void setAccountBalance(){
        if(Currency.getUserCurrencies()!=null) {
            int primId = Main.currentUser.getPrimaryCurrency();
            double primValue = Currency.getCurrencyValue(primId);
            String name = null;
            userCurrencies = Currency.getUserCurrencies();
            currencyValues = new double[userCurrencies.length];
            amounts = new double[userCurrencies.length];
            String[] names = Currency.getUserCurrencyNames();
            for (int i = 0; i < userCurrencies.length; i++) {
                currencyValues[i] = Currency.getCurrencyValue(userCurrencies[i]);
                amounts[i] = getAmount(userCurrencies[i]);
                double tempUSDAmount = currencyValues[i] * amounts[i];
                usdAmount = usdAmount + (tempUSDAmount);
                primAmount = tempUSDAmount/primValue;
                String selectSQL = "SELECT CURRENCYNAME FROM CURRENCYVALUE WHERE CURRENCYVALUE = ?;";
                try{
                    ps = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
                    ps.setDouble(1, currencyValues[i]);
                    rs = ps.executeQuery();
                    name = rs.getString("CURRENCYNAME");
                } catch (SQLException e){

                }
                pieChartData.add(new PieChart.Data(name, primAmount));
            }
            cur_piechart.setData(pieChartData);

            accountBalance.setText(String.format("%.2f", usdAmount / primValue));
        }
    }

     private double getAmount(int currencyId){
        String getCurrencyAmount = "SELECT AMOUNTOFCURRENCY FROM ACCOUNTCURRENCIES WHERE USERID = ? AND CURRENCYID = ?;";
        try{
            ps = CryptoBudgetDatabase.connection.prepareStatement(getCurrencyAmount);
            ps.setInt(1, Main.currentUser.getUserId());
            ps.setInt(2, currencyId);
            rs = ps.executeQuery();
            amount = rs.getDouble("AMOUNTOFCURRENCY");
        } catch (SQLException e){

        }
        return amount;
     }

     private void setAmountLabels(){
        String selectSQL = "SELECT CURRENCYNAME FROM CURRENCYVALUE WHERE CURRENCYID = ?;";
        try{
            ps = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
            ps.setInt(1, Main.currentUser.getPrimaryCurrency());
            rs = ps.executeQuery();
            accountBalance.setText(accountBalance.getText() + "   " +rs.getString("CURRENCYNAME") + "S");
         } catch (SQLException e){

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
