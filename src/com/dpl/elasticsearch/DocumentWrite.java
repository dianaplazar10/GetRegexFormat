package com.dpl.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * Document write using elasticsearch implementation 
 * class Author : Suvarna 
 * Created Date :  November 3rd 2016
 * 
 */

public class DocumentWrite {


    private static Client getClient() {
        final Settings.Builder settings = Settings.builder();
		TransportClient transportClient = TransportClient.builder().settings(settings).build();
        try {
			transportClient = TransportClient.builder().build()
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		        
		return transportClient;
    }
    public static void main(final String[] args) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	writeCompanies();
       writeInvestors();
       writeEmployees();
       writeInvestments();
    }
    
    public static void writeCompanies() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
    	 final Client client = getClient();
         // Create Index and set settings and mappings
         final String indexName = "fgcompanies";
         final String documentType = "companies";


         final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
         if (res.isExists()) {
             final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
             delIdx.execute().actionGet();
         }

         final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

         createIndexRequestBuilder.execute().actionGet();
         
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection connection = DriverManager.getConnection("jdbc:mysql://awstestdbinstance.cohsyjw90hi5.us-west-2.rds.amazonaws.com:3306/AWStestDB","root","findgoose2015");
                
         String query = "select * from fg_companytable";
         Statement statement = connection.createStatement();
         ResultSet result = statement.executeQuery(query);

         while (result.next()) {
         // Add documents
         final IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType,result.getString("cid") );
         // build json object
         final XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
         contentBuilder.field("cid", result.getString("cid"));
         contentBuilder.field("name", result.getString("name"));
         contentBuilder.field("revenue", result.getString("revenue"));
         contentBuilder.field("marketcap", result.getString("marketcap"));
         contentBuilder.field("pe_ratio", result.getString("pe_ratio"));
         contentBuilder.field("location", result.getString("location"));
         contentBuilder.field("technology", result.getString("technology"));
         contentBuilder.field("industry", result.getString("industry"));
         contentBuilder.field("company_logo", result.getString("company_logo"));
         contentBuilder.field("headcount", result.getString("headcount"));
         contentBuilder.field("investors", result.getString("investors"));
         contentBuilder.field("location", result.getString("location"));
         contentBuilder.field("technology", result.getString("technology"));
         contentBuilder.field("investors", result.getString("investors"));
         contentBuilder.field("industry", result.getString("industry"));
         contentBuilder.field("content", result.getString("name")+" "+result.getString("investors"));
         
         contentBuilder.field("crunchbase_link", result.getString("crunchbase_link"));
         contentBuilder.field("twitter_link", result.getString("twitter_link"));
         contentBuilder.field("angel_link", result.getString("angel_link"));
         contentBuilder.field("linkedin_link", result.getString("linkedin_link"));
         contentBuilder.field("blog_link", result.getString("blog_link"));
         

         indexRequestBuilder.setSource(contentBuilder);
         indexRequestBuilder.execute().actionGet();
         }
    }
    
    public static void writeInvestors(){
    	 final Client client = getClient();
         // Create Index and set settings and mappings
         final String indexName = "fginvestors";
         final String documentType = "investors";


         final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
         if (res.isExists()) {
             final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
             delIdx.execute().actionGet();
         }

         final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

         createIndexRequestBuilder.execute().actionGet();
         try{
         
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection connection = DriverManager.getConnection("jdbc:mysql://awstestdbinstance.cohsyjw90hi5.us-west-2.rds.amazonaws.com:3306/AWStestDB","root","findgoose2015");
                
         String query = "select * from fg_investortable";
         Statement statement = connection.createStatement();
         ResultSet result = statement.executeQuery(query);

         while (result.next()) {
         // Add documents
         final IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType,result.getString("investor_name") );
         // build json object
         final XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
         contentBuilder.field("investor_name", result.getString("investor_name"));
         contentBuilder.field("investedCompanies", result.getString("investedCompanies"));
         contentBuilder.field("investorType", result.getString("investorType"));
         contentBuilder.field("content", result.getString("investor_name"));
         
         indexRequestBuilder.setSource(contentBuilder);
         indexRequestBuilder.execute().actionGet();
         }
    }
    
    catch(Exception e){
    	e.printStackTrace();
    }
    }
    public static void writeInvestments(){
   	 final Client client = getClient();
        // Create Index and set settings and mappings
        final String indexName = "fginvestments";
        final String documentType = "investments";


        final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
            delIdx.execute().actionGet();
        }

        final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

        createIndexRequestBuilder.execute().actionGet();
        try{
        
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection("jdbc:mysql://awstestdbinstance.cohsyjw90hi5.us-west-2.rds.amazonaws.com:3306/AWStestDB","root","findgoose2015");
               
        String query = "select * from fg_investmenttable";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        while (result.next()) {
        // Add documents
        final IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType,result.getString("iid") );
        // build json object
        final XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
        contentBuilder.field("iid", result.getString("iid"));
        contentBuilder.field("cid", result.getString("cid"));
        contentBuilder.field("investee_name", result.getString("investee_name"));
        contentBuilder.field("amount", result.getString("amount"));
        contentBuilder.field("round", result.getString("round"));
        contentBuilder.field("investor_type", result.getString("investor_type"));
        contentBuilder.field("content", result.getString("cid"));
            
        indexRequestBuilder.setSource(contentBuilder);
        indexRequestBuilder.execute().actionGet();
        }
   }
   
   catch(Exception e){
   	e.printStackTrace();
   }
   }
    public static void writeEmployees(){
      	 final Client client = getClient();
           // Create Index and set settings and mappings
           final String indexName = "fgemployees";
           final String documentType = "employees";


           final IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
           if (res.isExists()) {
               final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
               delIdx.execute().actionGet();
           }

           final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

           createIndexRequestBuilder.execute().actionGet();
           try{
           
           Class.forName("com.mysql.jdbc.Driver").newInstance();
           Connection connection = DriverManager.getConnection("jdbc:mysql://awstestdbinstance.cohsyjw90hi5.us-west-2.rds.amazonaws.com:3306/AWStestDB","root","findgoose2015");
                  
           String query = "select * from fg_employeetable";
           Statement statement = connection.createStatement();
           ResultSet result = statement.executeQuery(query);

           while (result.next()) {
           // Add documents
           final IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType,result.getString("eid") );
           // build json object
           final XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
           contentBuilder.field("eid", result.getString("eid"));
           contentBuilder.field("cid", result.getString("cid"));
           contentBuilder.field("name", result.getString("name"));
           contentBuilder.field("designation", result.getString("designation"));
           contentBuilder.field("linkedin", result.getString("linkedin"));
           contentBuilder.field("education", result.getString("education"));
           contentBuilder.field("experience", result.getString("experience"));
           contentBuilder.field("content", result.getString("cid"));
                         
           indexRequestBuilder.setSource(contentBuilder);
           indexRequestBuilder.execute().actionGet();
           }
      }
      
      catch(Exception e){
      	e.printStackTrace();
      }
      }
}
