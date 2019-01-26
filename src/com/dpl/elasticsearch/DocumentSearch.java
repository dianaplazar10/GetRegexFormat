package com.dpl.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;


public class DocumentSearch {

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
	 public static void searchDoc(String name,Client client){
	    	SearchResponse response = client.prepareSearch("fgcompanies")
	    		    .setSearchType(SearchType.QUERY_AND_FETCH)
	    		    .setQuery(QueryBuilders.queryStringQuery(name).field("_all"))
	    		    .setFrom(0).setSize(60).setExplain(true)
	    		    .execute()
	    		    .actionGet();
	    		SearchHit[] results = response.getHits().getHits();
	    		for (SearchHit hit : results) {
	    		  System.out.println(hit.getSourceAsString());
	    		  Map<String,Object> result = hit.getSource();   //the retrieved document
	    		  if((result.get("name").toString().toLowerCase()).equals(name.toLowerCase())){
	    			  System.out.println("key check");
	    		  }
	    		  
	    		}
	    			    		
	    }
	 
	  public static void main(final String[] args) throws IOException, InterruptedException {

	        final Client client = getClient();
	       
	        searchDoc("google",client);
	        
	  }
	 
}
