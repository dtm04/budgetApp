package sample;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

public class CurrencyObj {
    protected String name;
    protected double value;
    protected String abbr;
    protected String percentChange;


    public CurrencyObj(String name, double value, String abbr, String percentChange){
        this.name = name;
        this.value = value;
        this.abbr = abbr;
        this.percentChange = percentChange;
    }

    public String getName(){
        return name;
    }

    public double getValue(){
        return value;
    }

//    public String getLastUpdate(){
//        if(lastUpdate == 0){
//            return "Never Updated";
//        }
//
//        Date date = new Date(lastUpdate);
//        Format format = new SimpleDateFormat("MM dd HH:mm:ss");
//        return format.format(date);
//    }

    public String getPercentChange(){
        return percentChange;
    }

    public String getAbbr(){
        return abbr;
    }

}
