package sample;

import com.jfoenix.controls.JFXProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
public class Goal {
    protected int goalId;
    protected int userId;
    protected String goalName;
    protected double finalGoal;
    protected String goalDate;
    protected String goalDescription;
    protected boolean isDone;
    protected double currentAmount;
    protected int currencyType;
    protected String currencyAbbreviation;
    protected double goalProgress;
    protected JFXProgressBar progressBar = new JFXProgressBar();

    /*
     * Default constructor for a sample.Goal
     */
    public Goal(){
        //this.setGoalId(0);
        this.setUserId(0000);
        this.setGoalName("DEFAULT NAME");
        this.setFinalGoal(10.0);
        this.setGoalDate("01-01-2011");
        this.setGoalDescription("DEFAULT DESCRIPTION");
        this.setDone(false);
        this.setCurrentAmount(0.00);
        this.setProgressBar(0);
        this.setCurrencyType(1);
    }

    /*
     * Constructor which takes a name, amount, and description
     * @param name -- name for the goal
     * @param description -- describes the nature of the goal
     */
    public Goal(String name, double amount, String date, String description){
        new Goal();
        this.setGoalDate(date);
        this.setGoalName(name);
        this.setFinalGoal(amount);
        this.setGoalDescription(description);
    }

    /*
     * Constructor which takes a name, amount, description, and progress bar
     * @param name -- name for the goal
     * @param description -- describes the nature of the goal
     */
    public Goal(String name, double amount, String date, String description, double progress){
        new Goal();
        this.setGoalDate(date);
        this.setGoalName(name);
        this.setFinalGoal(amount);
        this.setGoalDescription(description);
        this.setProgressBar(progress);
    }

    //result.add(new Goal(id, userId, name, amount, date, description, isDone, currentAmount))
    public Goal(int goalId, int userId, String name, double amount, String date,
                String description, boolean isDone, double currentAmount, int currencyType){
        new Goal();
        this.goalId = goalId;
        this.setUserId(userId);
        this.setGoalName(name);
        this.setFinalGoal(amount);
        this.setGoalDate(date);
        this.setGoalDescription(description);
        this.setDone(isDone);
        this.setCurrentAmount(currentAmount);
        this.setCurrencyType(currencyType);
    }

    /*
     * @return the value of the primary key ID of the goal
     */
    public int getGoalId(){
        return goalId;
    }

    /*
     * @return the value of the foreign key user ID for the goal
     */
    public int getUserId(){
        return userId;
    }

    /*
     * @return the value for the name of the goal
     */
    public String getGoalName(){
        return goalName;
    }

    /*
     * @return the value of the amount the goal is for
     */
    public double getFinalGoal(){
        return finalGoal;
    }

    /*
     * @return the value for the end date of the goal
     */
    public String getGoalDate(){
        return goalDate;
    }

    /*
     * @return the value of the description of the goal
     */
    public String getGoalDescription(){
        return goalDescription;
    }

    /*
     * @return the currency type for the goal
     */
    public int getCurrencyType() { return currencyType; }

    /*
     * @return the value of the current amount of the goal amount
     */
    public double getCurrentAmount(){
        return currentAmount;
    }

    public String getCurrencyAbbreviation() {
        return currencyAbbreviation;
    }

    public JFXProgressBar getProgressBar() { return progressBar; }

    public double getGoalProgress() { return currentAmount / finalGoal; }

    /*
     * @return value for whether or not goal is completed
     */
    public boolean isDone(){
        //TODO: if isDone then deleteGoal()
        if(currentAmount >= finalGoal){
            isDone = true;
        }
        else{
            isDone = false;
        }
        return isDone;
    }

    /*
     * assign a user ID to the goal
     * @param userIdToSet -- user ID for the goal
     */
    public void setUserId(int userIdToSet){
        if(userId >= 0){
            userId = userIdToSet;
        }
    }

    /*
     * assign a name to the goal
     * @param nameToSet -- the name for the goal
     */
    public void setGoalName(String nameToSet){
        goalName = nameToSet;
    }

    /*
     * assign a currency amount for the goal
     * @param amountToSet -- the amount for the goal
     */
    public void setFinalGoal(double amountToSet){
        finalGoal = amountToSet;
    }

    /*
     * sets the date for the goal to be completed
     * @param dateToSet -- end date for goal
     */
    public void setGoalDate(String dateToSet){
        goalDate = dateToSet;
    }

    /*
     * sets a description for the goal
     * @param descriptionToSet -- value describing the goal
     */
    public void setGoalDescription(String descriptionToSet){
        goalDescription = descriptionToSet;
    }

    /*
     * setting value for whether or not goal is completed
     * @param status -- value representing completion status
     */
    public void setDone(boolean status){
        isDone = status;
    }

    /*
     * set the current amount of goal progress
     * @param currentAmountToSet -- value for the currency amount
     */
    public void setCurrentAmount(double currentAmountToSet){
        currentAmount = currentAmountToSet;
    }

    //add an amount of currency to the goal progress
    public void addToCurrentAmount(double amount){
        currentAmount += amount;
    }

    public void setProgressBar(double progress) { progressBar.setProgress(progress); }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
        currencyAbbreviation = Currency.idToAbbr(currencyType);
    }

    @Override
    public String toString(){
        return String.format(goalId + ": " + goalName);
    }

    /*
     * database methods
     */
    public void create(){
        try{
            String sql = "INSERT INTO GOALS (USERID, GOALNAME, GOALAMOUNT, GOALDATE, GOALDESCRIPTION, " +
                    "ISDONE, CURRENTAMOUNT, CURRENCYTYPE)" + "VALUES (?, ?, ?, DATE(?), ?, ?, ?, ?)";
            PreparedStatement ps = CryptoBudgetDatabase.connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, goalName);
            ps.setDouble(3, finalGoal);
            ps.setString(4, goalDate); //change goalDate to type int, or to  type date in cbdb.java
            ps.setString(5, goalDescription);
            ps.setBoolean(6, isDone);
            ps.setDouble(7, currentAmount);
            ps.setInt(8, currencyType);
            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    //using the primary key to update the amount for a goal
    public void updateProgress(double amt){
        try {
            this.addToCurrentAmount(amt);
            this.isDone();
            String sql = "UPDATE GOALS SET GOALAMOUNT = ? WHERE GOALID = ?";
            PreparedStatement ps = CryptoBudgetDatabase.connection.prepareStatement(sql);
            ps.setDouble(1, currentAmount);
            ps.setInt(2, goalId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    //sets description for the goal given a primary key ID
    public void updateDescription(String note){
        try{
            String sql = "UPDATE GOALS SET GOALDESCRIPTION = ? WHERE GOALID = ?";
            PreparedStatement ps = CryptoBudgetDatabase.connection.prepareStatement(sql);
            ps.setString(1, note);
            ps.setInt(2, goalId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    //deletes a goal from the database
    public void deleteGoalById(int deleteId){
        try{
            String sql = "DELETE FROM GOALS WHERE GOALID = ?";
            PreparedStatement ps = CryptoBudgetDatabase.connection.prepareStatement(sql);
            ps.setInt(1, deleteId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void update(){
        try {
            String sql = "UPDATE GOALS SET GOALAMOUNT = ?, GOALDATE = ?, CURRENTAMOUNT = ?, CURRENCYTYPE = ? " +
                    "WHERE GOALID = ? AND USERID = ?;";
            PreparedStatement ps = CryptoBudgetDatabase.connection.prepareStatement(sql);
            ps.setDouble(1, finalGoal);
            ps.setString(2, goalDate);
            ps.setDouble(3, currentAmount);
            ps.setInt(4, currencyType);
            ps.setInt(5, goalId);
            ps.setInt(6, Main.currentUser.getUserId());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void remove(){
        try {
            //Currency.updateCurrencyAmount(currencyType, amount);
            String remove = "DELETE FROM GOALS WHERE GOALID = ? AND USERID = ?;";
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(remove);
            prep.setInt(1, goalId);
            prep.setInt(2, Main.currentUser.getUserId());
            prep.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public static Goal getGoalById(int findGoalId){
        try {
            String getAll = String.format("SELECT * FROM GOALS WHERE GOALID = ? AND USERID = ?;");
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(getAll);
            prep.setInt(1, findGoalId);
            prep.setInt(2, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("GOALID");
                int userId = rs.getInt("USERID");
                String name = rs.getString("GOALNAME");
                double amount = rs.getDouble("GOALAMOUNT");
                String date = rs.getString("GOALDATE");
                String description = rs.getString("GOALDESCRIPTION");
                boolean isDone = rs.getBoolean("ISDONE");
                double currentAmount = rs.getDouble("CURRENTAMOUNT");
                int currencyType = rs.getInt("CURRENCYTYPE");
                Goal result = new Goal(id, userId, name, amount, date, description, isDone, currentAmount, currencyType);
                return result;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Goal[] getAllGoals() {
        try {
            String getAll = String.format("SELECT * FROM GOALS WHERE USERID = ?;");
            PreparedStatement prep = CryptoBudgetDatabase.connection.prepareStatement(getAll);
            prep.setInt(1, Main.currentUser.getUserId());
            ResultSet rs = prep.executeQuery();
            ArrayList<Goal> result = new ArrayList();
            while (rs.next()) {
                int id = rs.getInt("GOALID");
                int userId = rs.getInt("USERID");
                String name = rs.getString("GOALNAME");
                double amount = rs.getDouble("GOALAMOUNT");
                String date = rs.getString("GOALDATE");
                String description = rs.getString("GOALDESCRIPTION");
                boolean isDone = rs.getBoolean("ISDONE");
                double currentAmount = rs.getDouble("CURRENTAMOUNT");
                int currencyType = rs.getInt("CURRENCYTYPE");
                result.add(new Goal(id, userId, name, amount, date, description, isDone, currentAmount, currencyType));
            }
            return result.toArray(new Goal[0]);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

}
