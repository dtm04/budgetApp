package sample;

import java.sql.*;
import java.util.ArrayList;

public class Payment extends Transaction {

    /*
        Constructors
     */
    public Payment() {
        super();
    }

    public Payment(int newCurrencyType, double newValue) {
        super(newCurrencyType, newValue);
    }

    //constructor used for ViewTransactionsController
    public Payment(int newCurrencyType, double newAmount, String newOtherParty, long newDate) {
        super(newCurrencyType, newAmount, newOtherParty, newDate, "Payment");
    }

    public Payment(int id, int userId, int currencyType, double amount, long date, String otherParty) {
        super(id, userId, currencyType, amount, date, otherParty);
    }

    /*
        Database & Logic
     */
    public void create() {
        try {
            String savePayment = "INSERT INTO PAYMENT (USERID, AMOUNT, DATE, CURRENCYTYPE, " +
                    "OTHERPARTY) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(savePayment);
            prep.setInt(1, userId);
            prep.setDouble(2, amount);
            prep.setLong(3, date);
            prep.setInt(4, currencyType);
            prep.setString(5, otherParty);
            prep.executeUpdate();
            Currency.updateCurrencyAmount(currencyType, -amount);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void update() {
        try {
            // Get previous amount value
            PreparedStatement prev = CryptoBudgetDatabase.connection.prepareStatement("SELECT AMOUNT, CURRENCYTYPE FROM PAYMENT " +
                    "WHERE PAYMENTID = ? AND USERID = ?;");
            prev.setInt(1, id);
            prev.setInt(2, Main.currentUser.getUserId());
            ResultSet rs = prev.executeQuery();
            rs.next();
            double prevAmount = rs.getDouble("AMOUNT");
            int prevCurrency = rs.getInt("CURRENCYTYPE");

            String update = "UPDATE PAYMENT SET AMOUNT = ?, DATE = ?, CURRENCYTYPE = ?, OTHERPARTY = ? " +
                    "WHERE PAYMENTID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(update);
            prep.setDouble(1, amount);
            prep.setLong(2, date);
            prep.setInt(3, currencyType);
            prep.setString(4, otherParty);
            prep.setInt(5, id);
            prep.setInt(6, Main.currentUser.getUserId());
            prep.executeUpdate();

            if (prevCurrency != currencyType) {
                Currency.updateCurrencyAmount(prevCurrency, prevAmount);
                Currency.updateCurrencyAmount(currencyType, -amount);
            } else {
                Currency.updateCurrencyAmount(currencyType, -(amount - prevAmount));
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public static Payment[] getAllPayments() {
        try {
            String getAll = String.format("SELECT * FROM PAYMENT WHERE USERID = ?;");
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(getAll);
            prep.setInt(1, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            ArrayList<Payment> result = new ArrayList();
            while (rs.next()) {
                int id = rs.getInt("PAYMENTID");
                int userId = rs.getInt("USERID");
                double amount = rs.getDouble("AMOUNT");
                long date = rs.getLong("DATE");
                int currencyType = rs.getInt("CURRENCYTYPE");
                String otherParty = rs.getString("OTHERPARTY");
                result.add(new Payment(id, userId, currencyType, amount, date, otherParty));
            }
            return result.toArray(new Payment[0]);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Payment getPayment(int findId) {
        try {
            String findPayment = "SELECT * FROM PAYMENT WHERE PAYMENTID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(findPayment);
            prep.setInt(1, findId);
            prep.setInt(2, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("PAYMENTID");
                int userId = rs.getInt("USERID");
                double amount = rs.getDouble("AMOUNT");
                long date = rs.getLong("DATE");
                int currencyType = rs.getInt("CURRENCYTYPE");
                String otherParty = rs.getString("OTHERPARTY");
                Payment result = new Payment(id, userId, currencyType, amount, date, otherParty);
                return result;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public void remove() {
        try {
            Currency.updateCurrencyAmount(currencyType, amount);
            String remove = "DELETE FROM PAYMENT WHERE PAYMENTID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(remove);
            prep.setInt(1, id);
            prep.setInt(2, Main.currentUser.getUserId());
            prep.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

}
