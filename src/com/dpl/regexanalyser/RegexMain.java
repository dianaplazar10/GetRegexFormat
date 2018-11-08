package com.dpl.regexanalyser;

/**
 * @author diana.lazar *
 * This is the startup class that handles the configuration of embedded 
 * jetty server and the servlets configurations within .
 */

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class RegexMain {

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger( RegexMain.class );
        PropertyConfigurator.configure("./conf/log4j.properties");
        configureApp();
        log.info("Regex Analyser application started successfully");
    }
    
    private static void configureApp()  throws Exception {
        Properties appProperties = ConstantUtil.readPropertiesFile(ConstantUtil.APP_PROP_FILE);
        ConstantUtil.init(appProperties);
        Server jettyServer = new Server(Integer.parseInt(ConstantUtil.SERVER_PORT));
        
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/RegexAnalyser");
        context.addServlet(RegexServlet.class,"/RegexServlet");
        
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"test.html"});
        resourceHandler.setResourceBase("WebContent/");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context });
        jettyServer.setHandler(handlers);

        jettyServer.start();
    }

}
