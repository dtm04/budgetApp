package sample;

import java.sql.*;
import java.util.ArrayList;

public class Income extends Transaction {

    /*
        Constructors
     */
    public Income() {
        super();
    }

    public Income(int newCurrencyType, double newValue) {
        super(newCurrencyType, newValue);
    }

    //contructor used for ViewTransactionsController
    public Income(int newCurrencyType, double newAmount, String newOtherParty, long newDate) {
        super(newCurrencyType, newAmount, newOtherParty, newDate, "Income");
    }

    public Income(int id, int userId, int currencyType, double amount, long date, String otherParty) {
        super(id, userId, currencyType, amount, date, otherParty);
    }

    /*
        Database & Logic
     */
    public void create() {
        try {
            String create = "INSERT INTO INCOME (USERID, AMOUNT, DATE, CURRENCYTYPE, " +
                    "OTHERPARTY) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(create);
            prep.setInt(1, userId);
            prep.setDouble(2, amount);
            prep.setLong(3, date);
            prep.setInt(4, currencyType);
            prep.setString(5, otherParty);
            prep.executeUpdate();
            Currency.updateCurrencyAmount(currencyType, amount);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void update() {
        try {
            // Get previous amount value
            PreparedStatement prev = CryptoBudgetDatabase.connection.prepareStatement("SELECT AMOUNT, CURRENCYTYPE FROM INCOME " +
                    "WHERE INCOMEID = ? AND USERID = ?;");
            prev.setInt(1, id);
            prev.setInt(2, Main.currentUser.getUserId());
            ResultSet rs = prev.executeQuery();
            rs.next();
            double prevAmount = rs.getDouble("AMOUNT");
            int prevCurrency = rs.getInt("CURRENCYTYPE");

            String update = "UPDATE INCOME SET AMOUNT = ?, DATE = ?, CURRENCYTYPE = ?, OTHERPARTY = ? " +
                    "WHERE INCOMEID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(update);
            prep.setDouble(1, amount);
            prep.setLong(2, date);
            prep.setInt(3, currencyType);
            prep.setString(4, otherParty);
            prep.setInt(5, id);
            prep.setInt(6, Main.currentUser.getUserId());
            prep.executeUpdate();

            if (prevCurrency != currencyType) {
                Currency.updateCurrencyAmount(prevCurrency, -prevAmount);
                Currency.updateCurrencyAmount(currencyType, amount);
            } else {
                Currency.updateCurrencyAmount(currencyType, amount - prevAmount);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public static Income[] getAllIncome() {
        try {
            String getAll = String.format("SELECT * FROM INCOME WHERE USERID = ?;");
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(getAll);
            prep.setInt(1, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            ArrayList<Income> result = new ArrayList();
            while (rs.next()) {
                int id = rs.getInt("INCOMEID");
                int userId = rs.getInt("USERID");
                double amount = rs.getDouble("AMOUNT");
                long date = rs.getLong("DATE");
                int currencyType = rs.getInt("CURRENCYTYPE");
                String otherParty = rs.getString("OTHERPARTY");
                result.add(new Income(id, userId, currencyType, amount, date, otherParty));
            }
            return result.toArray(new Income[0]);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Income getIncome(int findId) {
        try {
            String findIncome = "SELECT * FROM INCOME WHERE INCOMEID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(findIncome);
            prep.setInt(1, findId);
            prep.setInt(2, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("INCOMEID");
                int userId = rs.getInt("USERID");
                double amount = rs.getDouble("AMOUNT");
                long date = rs.getLong("DATE");
                int currencyType = rs.getInt("CURRENCYTYPE");
                String otherParty = rs.getString("OTHERPARTY");
                Income result = new Income(id, userId, currencyType, amount, date, otherParty);
                return result;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public void remove() {
        try {
            Currency.updateCurrencyAmount(currencyType, -amount);
            Statement stmt = CryptoBudgetDatabase.connection.createStatement();
            String remove = "DELETE FROM INCOME WHERE INCOMEID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(remove);
            prep.setInt(1, id);
            prep.setInt(2, Main.currentUser.getUserId());
            prep.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

}
