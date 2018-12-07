package sample;

import java.sql.*;

public class CryptoBudgetDatabase {
    public static Connection connection;

    private static String[] govCurrencyNames = {"US Dollar",
            "Euro",
            "British Pound",
            "Indian Rupee",
            "Australian Dollar",
            "Canadian Dollar",
            "Singapore Dollar",
            "Swiss Franc",
            "Malaysian Ringgit",
            "Japanese Yen",
            "Chinese Yuan Renminbi"};

    private static double[] govCurrencyValues = {   1.00,
            1.231748,
            1.397103,
            0.015434,
            0.784366,
            0.789020,
            0.757368,
            1.066661,
            0.255742,
            0.009289,
            0.157754};

    private static String[] govCurrencyAbbr = { "USD",
            "EUR",
            "GBP",
            "INR",
            "AUD",
            "CAD",
            "SGD",
            "CHF",
            "MYR",
            "JPY",
            "CNY"};

    private static Connection connect() {
        //saving the database in the src folder
        String dir = (System.getProperty("user.dir"));
        String OS = System.getProperty("os.name").toLowerCase();
        String url =  String.format("jdbc:sqlite:%s/src/CryptoBudget.db", dir);
        Connection connection = null;

        try{
            connection = DriverManager.getConnection(url);
        } catch(java.sql.SQLException e){
            //e.printStackTrace();
        }

        return connection;
    }

    public void createDatabase() {
        connection = connect();
        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            String accountLedger = "CREATE TABLE IF NOT EXISTS ACCOUNTLEDGER " +
                    "(USERID             INTEGER        PRIMARY KEY    NOT NULL," +
                    " ENABLEOCR          NUMERIC                       NOT NULL," +
                    " PRIMARYCURRENCY    INTEGER                       NOT NULL," +
                    " REFRESHRATE        TEXT                          NOT NULL," +
                    " USERNAME           TEXT                          NOT NULL," +
                    " PASSWORD           TEXT                          NOT NULL," +
                    " PASSWORDSALT       TEXT                          NOT NULL," +
                    " FIRSTNAME          TEXT," +
                    " LASTNAME           TEXT," +
                    " LASTLOGIN          INT," +
                    " FOREIGN KEY (PRIMARYCURRENCY)     REFERENCES CURRENCYVALUE(CURRENCYID));";

            stmt.executeUpdate(accountLedger);
            stmt.close();

            stmt = connection.createStatement();
            String currencyValue = "CREATE TABLE IF NOT EXISTS CURRENCYVALUE " +
                    "(CURRENCYID         INTEGER         PRIMARY KEY     NOT NULL," +
                    " CURRENCYNAME       TEXT                            NOT NULL," +
                    " CURRENCYVALUE      REAL                            NOT NULL," +
                    " LASTUPDATED        INT                             NOT NULL," +
                    " PERCENTCHANGE      TEXT                            NOT NULL," +
                    " CURRENCYABBREVIATION TEXT                          NOT NULL);";

            stmt.executeUpdate(currencyValue);
            stmt.close();

            stmt = connection.createStatement();
            String accountCurrencies = "CREATE TABLE IF NOT EXISTS ACCOUNTCURRENCIES " +
                    "(ACCOUNTCURRENCYID      INTEGER     PRIMARY KEY     NOT NULL," +
                    " USERID                 INTEGER                     NOT NULL," +
                    " CURRENCYID             INTEGER                     NOT NULL," +
                    " AMOUNTOFCURRENCY       REAL                        NOT NULL," +
                    " FOREIGN KEY (USERID)       REFERENCES ACCOUNTLEDGER(USERID)," +
                    " FOREIGN KEY (CURRENCYID)   REFERENCES CURRENCYVALUE(CURRENCYID));";

            stmt.executeUpdate(accountCurrencies);
            stmt.close();

            stmt = connection.createStatement();
            String payment = "CREATE TABLE IF NOT EXISTS PAYMENT " +
                    "(PAYMENTID            INTEGER         PRIMARY KEY     NOT NULL," +
                    " USERID               INTEGER                         NOT NULL," +
                    " AMOUNT               REAL                            NOT NULL," +
                    " DATE                 INT," +
                    " CURRENCYTYPE         INTEGER                         NOT NULL," +
                    " OTHERPARTY           TEXT," +
                    " FOREIGN KEY (USERID)         REFERENCES ACCOUNTLEDGER(USERID)," +
                    " FOREIGN KEY (CURRENCYTYPE)   REFERENCES CURRENCYVALUE(CURRENCYID));";

            stmt.executeUpdate(payment);
            stmt.close();

            stmt = connection.createStatement();
            String income = "CREATE TABLE IF NOT EXISTS INCOME " +
                    "(INCOMEID             INTEGER         PRIMARY KEY     NOT NULL," +
                    " USERID               INTEGER                         NOT NULL," +
                    " AMOUNT               REAL                            NOT NULL," +
                    " DATE                 INT," +
                    " CURRENCYTYPE         INTEGER                         NOT NULL," +
                    " OTHERPARTY           TEXT," +
                    " FOREIGN KEY (USERID)         REFERENCES ACCOUNTLEDGER(USERID)," +
                    " FOREIGN KEY (CURRENCYTYPE)   REFERENCES CURRENCYVALUE(CURRENCYID));";

            stmt.executeUpdate(income);
            stmt.close();

            stmt = connection.createStatement();
            String goals = "CREATE TABLE IF NOT EXISTS GOALS " +
                    "(GOALID          INTEGER         PRIMARY KEY     NOT NULL," +
                    " USERID          INTEGER                         NOT NULL," +
                    " GOALNAME        TEXT                            NOT NULL," +
                    " GOALAMOUNT      REAL                            NOT NULL," +
                    " GOALDATE        TEXT                            NOT NULL," +
                    " GOALDESCRIPTION TEXT," +
                    " ISDONE          NUMERIC                         NOT NULL," +
                    " CURRENTAMOUNT   REAL                            NOT NULL," +
                    " CURRENCYTYPE    INTEGER                         NOT NULL," +
                    " FOREIGN KEY (USERID)       REFERENCES ACCOUNTLEDGER(USERID));";

            stmt.executeUpdate(goals);
            stmt.close();

            String isThereSQL = "SELECT * FROM CURRENCYVALUE;";
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery((isThereSQL));

            if (!rs.isBeforeFirst()) {
                String sql = "INSERT INTO CURRENCYVALUE (CURRENCYNAME, CURRENCYVALUE, LASTUPDATED, PERCENTCHANGE, CURRENCYABBREVIATION) VALUES ( ? , ? , ? , ? , ? );";
                PreparedStatement pstmt = connection.prepareStatement(sql);

                for (int i = 0; i < govCurrencyNames.length; i++) {
                    pstmt.setString(1, govCurrencyNames[i].toUpperCase());
                    pstmt.setDouble(2, govCurrencyValues[i]);
                    pstmt.setInt(3, 0);
                    pstmt.setString(4, "None");
                    pstmt.setString(5, govCurrencyAbbr[i].toUpperCase());
                    pstmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            //System.out.println(e);
        }
    }
}
