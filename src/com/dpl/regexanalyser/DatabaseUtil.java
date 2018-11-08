package com.dpl.regexanalyser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author diana.lazar *  
 * This class will create and close the database connections required by the
 * application. This class will create connections for LYFT database
 * 
 */

public class DatabaseUtil {
    static final Logger log = Logger.getLogger(DatabaseUtil.class);
    public DatabaseUtil() {}

    Connection getConnection(String databaseURL) {
        Connection connection = null;
        try {
            Class.forName(ConstantUtil.DB_DRIVER_CLASS).newInstance();
            String dbUrl = databaseURL;
            connection = DriverManager.getConnection(dbUrl, ConstantUtil.DB_USERNAME, ConstantUtil.DB_PWD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    public static String covertPlexDt2DbDt(String inputDate)
    {
        String methName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String date = null;
        DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a"); //10/14/2015 3:16:00 PM
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            if(inputDate.isEmpty() || inputDate.equalsIgnoreCase(""))
                return null;
            else 
                date = outputFormat.format(inputFormat.parse(inputDate));
        }
        catch (ParseException e)
        {
            log.error(methName + "():" + e); 
        }
        return date;
    }
    
    public Timestamp getTimestampfromStringDate(String sinceDate){ 
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");//"MM/dd/yyyy");
        Date date = null;
        try {
            if(sinceDate.isEmpty() || sinceDate.equalsIgnoreCase(""))
                return null;
            date = formatter.parse(sinceDate);
        } catch (ParseException e) {
            log.error(e);
        }
        return (date == null ? null : new java.sql.Timestamp(date.getTime())); 
    }
    

}