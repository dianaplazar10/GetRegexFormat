package com.genze.returnschecklist;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author diana.lazar
 * This class loads all the constant strings for the application
 * when the application initialises.
 */

public class ConstantUtil {
    static final Logger log = Logger.getLogger(ConstantUtil.class);
    public static String SERVER_PORT = null;
    public static final String APP_PROP_FILE = "./conf/app.properties";
    
    public static String LYFT_DB_URL = null;
    public static String DB_USERNAME = null;
    public static String DB_PWD = null;
    public static String DB_DRIVER_CLASS = null;
    public static String PROC_INS_SERV_RETURNS=null;
    public static HashMap<String,String> FIELDS_MAP = new HashMap<String,String>();
    public static String IMGS_URL="";
    public static String REM_LOC_IMG_FOLDER="";
    public static String PROC_GET_VEH_RTND = "";
    public static String PROC_GET_VEH_RTND_FORDATE="";
    public static String RET_COND_1_NEWUNOPENEDBOX="";
    public static String RET_COND_2_REPACKEDBOX="";
    public static String RET_COND_3_LOOSE="";
    
    public static void init(final Properties appProperties) {
        SERVER_PORT = appProperties.getProperty("server.port");
        LYFT_DB_URL = appProperties.getProperty("lyftdb.url");
        DB_USERNAME = appProperties.getProperty("db.username");
        DB_PWD = appProperties.getProperty("db.password");
        DB_DRIVER_CLASS = appProperties.getProperty("db.driver");
        
        PROC_INS_SERV_RETURNS=appProperties.getProperty("f_ins_upd_vehicle_returns");
        REM_LOC_IMG_FOLDER=appProperties.getProperty("remotelocationfldr");
        PROC_GET_VEH_RTND=appProperties.getProperty("f_get_latest_vehicle_return_from_vehicle_number");
        PROC_GET_VEH_RTND_FORDATE=appProperties.getProperty("f_get_vehicle_returns_from_date");
        
        RET_COND_1_NEWUNOPENEDBOX=appProperties.getProperty("returncondition.1");
        RET_COND_2_REPACKEDBOX=appProperties.getProperty("returncondition.2");
        RET_COND_3_LOOSE=appProperties.getProperty("returncondition.3");
    }

    //This is the function which loads application.properties into configuration.
    public static Properties readPropertiesFile(String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        String methName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            inputStream = new FileInputStream(fileName);
            properties.load(inputStream);
        } catch (IOException ex) {
            log.error(methName + "():" + "Exception in Reading properites file");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(methName + "():" + "Exception in closing properites file");
                }
            }
        }
        return properties;
    }
    
}