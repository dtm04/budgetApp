package sample;

import java.sql.*;
import java.util.ArrayList;

public class Currency {

    private static int userId = Main.currentUser.getUserId();
    private static Connection connection = CryptoBudgetDatabase.connection;

    /**
     *
     * @param currencyId The id of the currency to search
     * @return The value of the currency
     */
    public static double getCurrencyValue(int currencyId){
        double currencyValue = 0.0;
        try{
            Statement stmt = connection.createStatement();
            String valueSQL = "SELECT CURRENCYVALUE FROM CURRENCYVALUE WHERE CURRENCYID = ?;";
            PreparedStatement pstmt = connection.prepareStatement(valueSQL);
            pstmt.setInt(1, currencyId);
            ResultSet rs = pstmt.executeQuery();

            currencyValue = rs.getDouble("CURRENCYVALUE");
        } catch (SQLException e) {

        }
        return currencyValue;
    }

    /**
     *
     * @param currencyName The name of the currency to search
     * @return the value of the currency
     */
    public static double getCurrencyValue(String currencyName){
        double currencyValue = 0.0;
        try{
            String valueSQL = "SELECT CURRENCYVALUE FROM CURRENCYVALUE WHERE CURRENCYNAME = ?;";
            PreparedStatement pstmt = connection.prepareStatement(valueSQL);
            pstmt.setString(1, currencyName);
            ResultSet rs = pstmt.executeQuery();

            currencyValue = rs.getDouble("CURRENCYVALUE");
        } catch (SQLException e) {

        }
        return currencyValue;
    }

    /**
     *
     * @return Get the currencyIDs of all the currencies the user has
     */
    public static int[] getUserCurrencies(){
        ArrayList<Integer> userCurrencies = new ArrayList<>();
        String getUserCurrencies = "SELECT CURRENCYID FROM ACCOUNTCURRENCIES WHERE USERID = ?;";
        try{
            PreparedStatement pstmt = connection.prepareStatement(getUserCurrencies);
            pstmt.setInt(1, userId);
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()){
                userCurrencies.add(rs.getInt("CURRENCYID"));
            }
        } catch (SQLException e){

        }

        int[] userCurrencyNames = new int[userCurrencies.size()];

        for(int i = 0; i < userCurrencyNames.length; i++){
            userCurrencyNames[i] = userCurrencies.get(i).intValue();
        }

        return userCurrencyNames;
    }

    public static String[] getUserCurrencyNames(){
        ArrayList<String> userCurrencies = new ArrayList<>();
        String getUserCurrencies = "SELECT CURRENCYNAME FROM ACCOUNTCURRENCIES WHERE USERID = ?;";
        try{
            PreparedStatement pstmt = connection.prepareStatement(getUserCurrencies);
            pstmt.setInt(1, userId);
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()){
                userCurrencies.add(rs.getString("CURRENCYNAME"));
            }
        } catch (SQLException e){

        }

        String[] userCurrencyNames = new String[userCurrencies.size()];

        for(int i = 0; i < userCurrencyNames.length; i++){
            userCurrencyNames[i] = userCurrencies.get(i);
        }

        return userCurrencyNames;
    }

    /**
     *  Update or insert the updated amount of currency a user has
     * @param currencyId The id of the currency
     * @param amount The value to add to the current amount of currency
     */
    public static void updateCurrencyAmount(int currencyId, double amount){
        if(isUserHasCurrency(currencyId)){
            //get how much they have then add the amount to it
            double currentAmount = 0.0;
            String getCurrencyAmount = "SELECT AMOUNTOFCURRENCY FROM ACCOUNTCURRENCIES WHERE USERID = ? AND CURRENCYID = ?;";
            try{
                PreparedStatement pstmt = CryptoBudgetDatabase.connection.prepareStatement(getCurrencyAmount);
                pstmt.setInt(1, Main.currentUser.getUserId());
                pstmt.setInt(2, currencyId);
                ResultSet rs = pstmt.executeQuery();

                currentAmount = rs.getDouble("AMOUNTOFCURRENCY");
            } catch (SQLException e){

            }

            currentAmount = currentAmount + amount;
            String updateUserCurrency = "UPDATE ACCOUNTCURRENCIES SET AMOUNTOFCURRENCY = ? WHERE USERID = ? AND CURRENCYID = ?;";

            try{
                PreparedStatement pstmt = CryptoBudgetDatabase.connection.prepareStatement(updateUserCurrency);
                pstmt.setDouble(1, currentAmount);
                pstmt.setInt(2, Main.currentUser.getUserId());
                pstmt.setInt(3, currencyId);

                pstmt.executeUpdate();
            } catch (SQLException e){

            }
        } else {
            //create a new row with the given amount
            String createNewAccountCurrency = "INSERT INTO ACCOUNTCURRENCIES (USERID, CURRENCYID, AMOUNTOFCURRENCY) VALUES ( ? , ? , ? )";
            try {
                PreparedStatement pstmt = CryptoBudgetDatabase.connection.prepareStatement(createNewAccountCurrency);
                pstmt.setInt(1, Main.currentUser.getUserId());
                pstmt.setInt(2, currencyId);
                pstmt.setDouble(3, amount);

                pstmt.executeUpdate();
            } catch (SQLException e) {

            }
        }
    }

    /**
     *  Update or insert the updated amount of currency a user has
     * @param currencyName The name of the currency
     * @param amount The value to add to the current amount of currency
     */
    public static void updateCurrencyAmount(String currencyName, double amount){
        //get the currency id then call updateCurrencyAmount with it
        String getCurrencyID = "SELECT CURRENCYID FROM CURRENCYVALUE WHERE CURRENCYNAME = ?";
        int currencyId = 0;
        try{
            PreparedStatement pstmt = connection.prepareStatement(getCurrencyID);
            pstmt.setString(1, currencyName);
            ResultSet rs = pstmt.executeQuery();

            currencyId = rs.getInt("CURRENCYID");
        } catch (SQLException e){

        }

        updateCurrencyAmount(currencyId, amount);
    }

    /**
     *
     * @param currencyID The id of the currency
     * @return True if the User ever owned this currency
     */
    public static boolean isUserHasCurrency(int currencyID){
        //check if it was already there
        String selectSQL = "SELECT * " +
                "FROM ACCOUNTCURRENCIES " +
                "WHERE USERID = ? " +
                "AND CURRENCYID = ?;";

        boolean userHasCurrency = false;

        try {
            PreparedStatement pstmt = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
            pstmt.setInt(1, Main.currentUser.getUserId());
            pstmt.setInt(2, currencyID);

            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()){
                userHasCurrency = true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return userHasCurrency;
    }

    /**
     *
     * @param currencyName The id of the currency
     * @return True if the User ever owned this currency
     */
    public static boolean isUserHasCurrency(String currencyName){
        //check if it was already there
        String selectSQL = "SELECT * " +
                "FROM ACCOUNTCURRENCIES " +
                "WHERE USERID = ? " +
                "AND CURRENCYNAME = ?;";

        boolean userHasCurrency = false;

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectSQL);
            pstmt.setInt(1, userId);
            pstmt.setString(2, currencyName);

            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()){
                userHasCurrency = true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return userHasCurrency;
    }

    /**
     *
     * @param abbr The abbreviation of a currency
     * @return The Currency Name if it exists or an empty string if it doesn't
     */
    public static String abbrToName(String abbr){
        String currencyName = "";
        if(abbr == ""){
            return currencyName;
        }
        String selectSQL = "SELECT CURRENCYNAME FROM CURRENCYVALUE WHERE CURRENCYABBREVIATION = ?;";
        try{
            PreparedStatement pstmt = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
            pstmt.setString(1, abbr);
            ResultSet rs = pstmt.executeQuery();
            currencyName = rs.getString("CURRENCYNAME");
        } catch (SQLException e){

        }

        return currencyName;
    }

    /**
     *
     * @param currencyName The currency name to check
     * @return True if the currency name is valid
     */
    public static boolean isCurrencyName(String currencyName){
        boolean isValid = false;

        String selectSQL = "SELECT * FROM CURRENCYVALUE WHERE CURRENCYNAME = ?;";

        try{
            PreparedStatement pstmt = connection.prepareStatement(selectSQL);
            pstmt.setString(1, currencyName);

            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()){
                isValid = true;
            }
        } catch (SQLException e){

        }

        return isValid;
    }

    public static int abbrToId(String abbr) {
        String selectSQL = "SELECT CURRENCYID FROM CURRENCYVALUE WHERE CURRENCYABBREVIATION = ?;";
        try {
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
            prep.setString(1, abbr);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                return rs.getInt("CURRENCYID");
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return -1;
    }

    public static String idToAbbr(int id) {
        String selectSQL = "SELECT CURRENCYABBREVIATION FROM CURRENCYVALUE WHERE CURRENCYID = ?;";
        try {
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(selectSQL);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                return rs.getString("CURRENCYABBREVIATION");
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return "";
    }
}