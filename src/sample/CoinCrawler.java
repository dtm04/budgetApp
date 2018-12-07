package sample;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.sql.*;


public class CoinCrawler {
    private static final OkHttpClient client = new OkHttpClient.Builder().build();
    private static java.sql.Connection db;
    private static ArrayList<String> userCoins;
    private static boolean searchAll;

    public CoinCrawler(ArrayList<String> userCoins, boolean searchAll, Connection db){
        this.userCoins = userCoins;
        this.searchAll = searchAll;
        this.db = db;
    }

    /**
     *
     * @param userCoins The coin names
     */
    public void setUserCoins(ArrayList<String> userCoins){
        this.userCoins = userCoins;
    }

    /**
     *
     * @param searchAll True if user wants to search all coins
     */
    public void setSearchAll(boolean searchAll){
        this.searchAll = searchAll;
    }

    /**
     *  Insert or update the value of a currency
     * @param name The name of the currency
     * @param value The value of the currency
     * @param percentChange The percent change over 24 hours
     */
    //add the row in cryptoValue if it is not already there
    private static void insertData(String name, String value, String percentChange, String abbr){
        if(!abbr.isEmpty()) {
            name = name.toUpperCase();
            value = value.replace(",", "");

            //check if it was already there
            String selectSQL = "SELECT * " +
                    "FROM CURRENCYVALUE " +
                    "WHERE currencyName = ?";

            boolean coinIsThere = false;

            try {
                PreparedStatement pstmt = db.prepareStatement(selectSQL);
                pstmt.setString(1, name);

                ResultSet rs = pstmt.executeQuery();

                if (rs.isBeforeFirst()) {
                    coinIsThere = true;
                } else {
                    coinIsThere = false;
                }
            } catch (SQLException e) {
                System.out.println("error");
                e.printStackTrace();
            }

            Date date = new Date();
            long timeMilli = date.getTime();
            String sql = "INSERT INTO CURRENCYVALUE (CURRENCYNAME, CURRENCYVALUE, LASTUPDATED, PERCENTCHANGE, CURRENCYABBREVIATION) VALUES ( ? , ? , ? , ? , ? )";

            if (coinIsThere) {
                sql = "UPDATE CURRENCYVALUE SET currencyValue = ? , percentChange = ?, lastUpdated = ? WHERE currencyName = ?";
            }
            try {
                PreparedStatement pstmt = db.prepareStatement(sql);
                if (coinIsThere) {
                    pstmt.setString(1, value);
                    pstmt.setString(2, percentChange);
                    pstmt.setLong(3, timeMilli);
                    pstmt.setString(4, name);
                } else {
                    pstmt.setString(1, name);
                    pstmt.setString(2, value);
                    pstmt.setLong(3, timeMilli);
                    pstmt.setString(4, percentChange);
                    pstmt.setString(5, abbr);
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the CURRENCYVALUE table based on userCoins and searchAll
     */
    public static void updateCoins(){
        try{
            doGetRequest("https://coinranking.com/", true);
        } catch (IOException e){

        }
    }

    private static void doGetRequest(String url, boolean isFirst) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String value;
                        String percentChange;
                        String upOrDown;
                        String abbr;
                        boolean isUp;
                        String html = response.body().string();

                        Elements coins = Jsoup.parse(html).select(".coin-list__body a .wrapper .grid");
                        for (Element coin : coins) {
                            String coinName = coin.select(".coin-name").text();
                            if(searchAll) {
                                value = coin.select(".coin-list__body__row__price__value").text();
                                abbr = coin.select(".coin-list__body__row__cryptocurrency__prepend__icon__img").attr("alt").replaceAll("(.+\\()", "");
                                abbr = abbr.replaceAll("(\\).+)", "");
                                upOrDown = coin.select(".coin-list__body__row__change img").attr("alt");
                                isUp = upOrDown.equals("24h change gone up");
                                percentChange = coin.select(".coin-list__body__row__change").text();

                                if (isUp) {
                                    percentChange = "+" + percentChange;
                                } else {
                                    percentChange = "-" + percentChange;
                                }
                                insertData(coinName, value, percentChange, abbr);
                            } else if(userCoins.contains(coinName)) {
                                //put this into the database
                                value = coin.select(".coin-list__body__row__price__value").text();
                                percentChange = coin.select(".coin-list__body__row__change").text();
                                abbr = coin.select(".coin-list__body__row__cryptocurrency__prepend__icon__img").attr("alt").replaceAll("(.+\\()", "");
                                abbr = abbr.replaceAll("(\\).+)", "");
                                upOrDown = coin.select(".coin-list__body__row__change img").attr("alt");
                                isUp = upOrDown.equals("24h change gone up");
                                if (isUp) {
                                    percentChange = "+" + percentChange;
                                } else {
                                    percentChange = "-" + percentChange;
                                }
                                userCoins.remove(coinName);

                                insertData(coinName, value, percentChange, abbr);
                            }
                            if(userCoins.isEmpty() && !searchAll){
                                break;
                            }
                        }
                        if(isFirst){
                            Elements totalPages = Jsoup.parse(html).select(".coin-list__footer .pagination__info b + b");
                            int currentPage = 2;
                            while (currentPage <= Integer.parseInt(totalPages.text())) {
                                String newPageURL = "https://coinranking.com/?page=" + String.valueOf(currentPage);
                                doGetRequest(newPageURL, false);
                                if(userCoins.isEmpty() && !searchAll){
                                    break;
                                }
                                currentPage++;
                            }
                        }
                    }
                });
    }
}

