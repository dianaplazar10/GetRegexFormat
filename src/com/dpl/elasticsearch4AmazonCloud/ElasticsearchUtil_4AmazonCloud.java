/*Author : Diana P Lazar
Date created : 19/11/2016
Copyright@ FindGoose
 */

package com.dpl.elasticsearch4AmazonCloud;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.DeleteIndex;

public class ElasticsearchUtil_4AmazonCloud {
	public final String FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX = ESMessages.getString("ES_messages.IndexMapping.companies"); //$NON-NLS-1$
	private final String FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX = ESMessages.getString("ES_messages.IndexMapping.investors"); //$NON-NLS-1$
	private final String FGDB_Investments_TypeName_FROM_awsCloud_MAIN_INDEX = ESMessages.getString("ES_messages.IndexMapping.investments"); //$NON-NLS-1$
	private final String FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX = ESMessages.getString("ES_messages.IndexMapping.compemployees"); //$NON-NLS-1$
	
    private final String awsCloud_MAIN_INDEX_NAME = ESMessages.getString("ES_messages.AmazonMainIndexName"); //$NON-NLS-1$
    public static void main(String[] args) {
        try {
        	 // Get Jest client
        	ElasticsearchUtil_4AmazonCloud esUtil = new ElasticsearchUtil_4AmazonCloud();
            JestClient jestClient = esUtil.getJESTclient4MainIndexInAmazonCloud();
            try {
                // run test index & searching
//            	
//            	String queryCompanyTable ="select * from fg_companytable";
//            	RunMeCustom.deleteDB_TableIndex(jestClient);
            	
//            	esUtil.indexDB_TableIndex(jestClient,"select * from fg_companytable"); //$NON-NLS-1$
//            	esUtil.indexDB_TableIndex(jestClient,"select * from fg_investortable"); //$NON-NLS-1$
//            	esUtil.indexDB_TableIndex(jestClient,"select * from fg_investmenttable"); //$NON-NLS-1$
//            	esUtil.indexDB_TableIndex(jestClient,"select * from fg_employeetable"); //$NON-NLS-1$

            	String searchString = "google";
            	
            	esUtil.read_IndexedDataFromCloud(jestClient,esUtil.FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX, searchString);
            	System.out.println("***************************************************"); //$NON-NLS-1$
            	esUtil.read_IndexedDataFromCloud(jestClient,esUtil.FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX, searchString);
            	System.out.println("***************************************************"); //$NON-NLS-1$
            	esUtil.read_IndexedDataFromCloud(jestClient,esUtil.FGDB_Investments_TypeName_FROM_awsCloud_MAIN_INDEX, searchString);
            	System.out.println("***************************************************"); //$NON-NLS-1$
            	esUtil.read_IndexedDataFromCloud(jestClient,esUtil.FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX, searchString);
            } finally {
                // shutdown client
                        jestClient.shutdownClient();
                System.out.println("***************************************************"); //$NON-NLS-1$
                System.out.println("Successfull write to Elasticsearch on amazon cloud!"); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	/**
	 * @return
	 */
	public JestClient getJESTclient4MainIndexInAmazonCloud() {
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(
				ESMessages.getString("ES_messages.AmazonMainIndexDomainURL")).multiThreaded(true).build(); //$NON-NLS-1$
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(clientConfig);
		JestClient jestClient = factory.getObject();
		return jestClient;
	}

    public void deleteDB_TableIndex(JestClient jestClient) {
	     try {
	    	 DeleteIndex deleteIndex = new DeleteIndex.Builder(awsCloud_MAIN_INDEX_NAME)
		                .build();
		        jestClient.execute(deleteIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  

    //This method does the indexing of the tables and writes them as 'mappings' to the amazon cloud main index
	public void indexDB_TableIndex(final JestClient jestClient, String query) {
       	Bulk.Builder bulkbuilder = new Bulk.Builder();
       	Bulk bulk = null;   
       	if(query.endsWith(ESMessages.getString("FG_CompaniesTableName"))) //$NON-NLS-1$
        {bulk = getBulk_for_Companies(jestClient,bulkbuilder,query);}
       	else if(query.endsWith(ESMessages.getString("FG_CompanyInvestorsTableName"))) //$NON-NLS-1$
        {bulk = getBulk_for_Investors(jestClient,bulkbuilder,query);}
       	else if(query.endsWith(ESMessages.getString("FG_CompanyInvestmentsTableName"))) //$NON-NLS-1$
        {bulk = getBulk_for_compInvestments(jestClient,bulkbuilder,query);}
       	else if(query.endsWith(ESMessages.getString("FG_CompanyEmployeesTableName"))) //$NON-NLS-1$
        {bulk = getBulk_for_compEmployees(jestClient,bulkbuilder,query);}
                
        @SuppressWarnings("unused")
		JestResult jestResult;
		try {
			jestResult = jestClient.execute(bulk);
			Thread.sleep(2000);
			//System.out.println(jestResult.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/*typeName_FROM_awsCloud_MAIN_INDEX -  refers to one of the following mappings that was created under the main index in cloud mentioned below:
	 * AmazonMainIndexName=fgindexes
	 * IndexMapping.companies=companies
	 * IndexMapping.investors=investors
	 * IndexMapping.investments=investments
	 * IndexMapping.compemployees=compemployees
	 * */
	 public void read_IndexedDataFromCloud(final JestClient jestClient, String typeName_FROM_awsCloud_MAIN_INDEX,String searchText) throws Exception {
		 	SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
	        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(searchText));//same as QueryBuilders.queryStringQuery("google")
	        Search search = new Search.Builder(searchSourceBuilder.toString())
			        .addIndex(awsCloud_MAIN_INDEX_NAME).addType(typeName_FROM_awsCloud_MAIN_INDEX).build();
			JestResult result = jestClient.execute(search);
			printSearchResultsFromIndexedCloudData(result, typeName_FROM_awsCloud_MAIN_INDEX, searchText);
	    }

	/**
	 * @param jestClient
	 * @param JestResult
	 * @param searchSourceBuilder
	 * @param awsCloud_typeName
	 * @throws IOException
	 */
	public void printSearchResultsFromIndexedCloudData(JestResult result, String awsCloud_typeName, String searchStr)
			throws IOException {
		if(awsCloud_typeName.equalsIgnoreCase(FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX))
		{
			List<CompanyInfo> companyNames = result.getSourceAsObjectList(CompanyInfo.class);
			if(companyNames.isEmpty()) System.out.println("No listings on this search(companyNames)"); //$NON-NLS-1$
			else{
				System.out.println("Companies relative to keyword : " +searchStr+ " are:");
				for (CompanyInfo companyName : companyNames) {
				    System.out.println(companyName);//Calls the toString() of CompanyInfo.
				}
			}
		}
		
		if(awsCloud_typeName.equalsIgnoreCase(FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX))
		{
			List<CompanyEmployeesInfo> companyEmployeesInfos = result.getSourceAsObjectList(CompanyEmployeesInfo.class);
			if(companyEmployeesInfos.isEmpty()) System.out.println("No listings on this search(Company Employees)"); //$NON-NLS-1$
			else{
				System.out.println("employess relative to company relative to each keyword : " +searchStr+ " are:");
				for (CompanyEmployeesInfo companyEmployeesInfo : companyEmployeesInfos) {
				    System.out.println(companyEmployeesInfo);//Calls the toString() of CompanyInfo.
				}
			}
		}
		
		if(awsCloud_typeName.equalsIgnoreCase(FGDB_Investments_TypeName_FROM_awsCloud_MAIN_INDEX))
		{
			List<InvestmentInfo4ESIndexing> investmentInfos4ESIndexing = result.getSourceAsObjectList(InvestmentInfo4ESIndexing.class);
			if(investmentInfos4ESIndexing.isEmpty()) System.out.println("No listings on this search(Investments)"); //$NON-NLS-1$
			else
			{
				System.out.println("Investments related to keyword : " +searchStr+ " are:");
				for (InvestmentInfo4ESIndexing investmentInfo4ESIndexing : investmentInfos4ESIndexing) {
					System.out.println(investmentInfo4ESIndexing);//Calls the toString() of CompanyInfo.
				}
			}
		}
		
		if(awsCloud_typeName.equalsIgnoreCase(FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX))
		{
			List<InvestorInfo> investorInfos = result.getSourceAsObjectList(InvestorInfo.class);
			if(investorInfos.isEmpty()) System.out.println("No listings on this search(investors)"); //$NON-NLS-1$
			else{
				System.out.println("Investors related to keyword : " +searchStr+ " are:");
				for (InvestorInfo investorInfo : investorInfos) {
				    System.out.println(investorInfo);//Calls the toString() of CompanyInfo.
				}
			}
		}
	}

	/**
	 * @param bulkbuilder
	 * @return 
	 */
	private Bulk getBulk_for_Companies(JestClient jestClient,Bulk.Builder bulkbuilder,String dbQueryString) {
		try {
			Class.forName(ESMessages.getString("MYSQL_DriverClassString")).newInstance(); //$NON-NLS-1$
			Connection connection = DriverManager.getConnection(ESMessages.getString("AmazonCloud_RDS.mysqlURL"),ESMessages.getString("AmazonCloud_RDS.mysql_rootIDstr"),ESMessages.getString("AmazonCloud_RDS.mysql_rootPWDstr")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String query = dbQueryString;//"select * from fg_companytable";
	        Statement statement = connection.createStatement();
	        ResultSet result = statement.executeQuery(query);
	        while(result.next()) {
	        	
	        	String companies_FGDB_TablePrimKey = result.getString("cid"); //$NON-NLS-1$
	        	if(companies_FGDB_TablePrimKey!=null)
	        	{
	        		CompanyInfo companyInfo = buildIndex4Companies(result);	        		
	        		bulkbuilder.addAction(new Index.Builder(companyInfo).index(awsCloud_MAIN_INDEX_NAME).type(FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX).build());
	        	}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return bulkbuilder.build();
	}
	
	
	/**
	 * @param bulkbuilder
	 * @return 
	 */
	private Bulk getBulk_for_Investors(JestClient jestClient,Bulk.Builder bulkbuilder,String dbQueryString) {
		try {
			Class.forName(ESMessages.getString("MYSQL_DriverClassString")).newInstance(); //$NON-NLS-1$
			Connection connection = DriverManager.getConnection(ESMessages.getString("AmazonCloud_RDS.mysqlURL"),ESMessages.getString("AmazonCloud_RDS.mysql_rootIDstr"),ESMessages.getString("AmazonCloud_RDS.mysql_rootPWDstr")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String query = dbQueryString;//"select * from fg_companytable";
	        Statement statement = connection.createStatement();
	        ResultSet result = statement.executeQuery(query);
	        while(result.next()) {
	        	
	        	String investors_FGDB_TablePrimKey = result.getString("investor_name"); //$NON-NLS-1$
	        	if(investors_FGDB_TablePrimKey!=null)
	        	{
	        		InvestorInfo investorInfo = buildIndex4Investors(result);	        		
	        		bulkbuilder.addAction(new Index.Builder(investorInfo).index(awsCloud_MAIN_INDEX_NAME).type(FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX).build());
	        	}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return bulkbuilder.build();
	}
	
	
	/**
	 * @param bulkbuilder
	 * @return 
	 */
	private Bulk getBulk_for_compInvestments(JestClient jestClient,Bulk.Builder bulkbuilder,String dbQueryString) {
		try {
			Class.forName(ESMessages.getString("MYSQL_DriverClassString")).newInstance(); //$NON-NLS-1$
			Connection connection = DriverManager.getConnection(ESMessages.getString("AmazonCloud_RDS.mysqlURL"),ESMessages.getString("AmazonCloud_RDS.mysql_rootIDstr"),ESMessages.getString("AmazonCloud_RDS.mysql_rootPWDstr")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String query = dbQueryString;//"select * from fg_companytable";
	        Statement statement = connection.createStatement();
	        ResultSet result = statement.executeQuery(query);
	        while(result.next()) {
	        	
	        	String investments_FGDB_TablePrimKey = result.getString("iid"); //$NON-NLS-1$
	        	String companies_FGDB_TablePrimKey = result.getString("cid"); //$NON-NLS-1$
	        	
	        	if(investments_FGDB_TablePrimKey!=null && companies_FGDB_TablePrimKey!=null)
	        	{
	        		InvestmentInfo4ESIndexing investmentInfo = buildIndex4Investments(result);	        		
	        		bulkbuilder.addAction(new Index.Builder(investmentInfo).index(awsCloud_MAIN_INDEX_NAME).type(FGDB_Investments_TypeName_FROM_awsCloud_MAIN_INDEX).build());
	        	}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return bulkbuilder.build();
	}
	
	
	/**
	 * @param bulkbuilder
	 * @return 
	 */
	private Bulk getBulk_for_compEmployees(JestClient jestClient,Bulk.Builder bulkbuilder,String dbQueryString) {
		try {
			Class.forName(ESMessages.getString("MYSQL_DriverClassString")).newInstance(); //$NON-NLS-1$
			Connection connection = DriverManager.getConnection(ESMessages.getString("AmazonCloud_RDS.mysqlURL"),ESMessages.getString("AmazonCloud_RDS.mysql_rootIDstr"),ESMessages.getString("AmazonCloud_RDS.mysql_rootPWDstr")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String query = dbQueryString;//"select * from fg_companytable";
	        Statement statement = connection.createStatement();
	        ResultSet result = statement.executeQuery(query);
	        while(result.next()) {
	        	
	        	String compEmployees_FGDB_TablePrimKey = result.getString("eid"); //$NON-NLS-1$
	        	String companies_FGDB_TablePrimKey = result.getString("cid"); //$NON-NLS-1$

	        	if(compEmployees_FGDB_TablePrimKey!=null && companies_FGDB_TablePrimKey!=null)
	        	{
	        		CompanyEmployeesInfo companyEmployeesInfo = buildIndex4CompanyEmployees(result);	        		
	        		bulkbuilder.addAction(new Index.Builder(companyEmployeesInfo).index(awsCloud_MAIN_INDEX_NAME).type(FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX).build());
	        	}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return bulkbuilder.build();
	}

	/**
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private CompanyInfo buildIndex4Companies(ResultSet result) throws SQLException {
		BigDecimal peRatioAsStr = StringUtil.parseStr2BigDecimal(result.getString("pe_ratio")); //$NON-NLS-1$
		final CompanyInfo companyInfo = new CompanyInfo(
				Integer.parseInt(result.getString("cid")), //$NON-NLS-1$
				result.getString("name"), //$NON-NLS-1$
				result.getString("revenue"), //$NON-NLS-1$
				result.getString("marketcap"), //$NON-NLS-1$
				peRatioAsStr,
				result.getString("location"), //$NON-NLS-1$
				result.getString("technology"), //$NON-NLS-1$
				result.getString("industry"), //$NON-NLS-1$
				result.getString("company_logo"), //$NON-NLS-1$
				Integer.parseInt(result.getString("headcount")), //$NON-NLS-1$
				result.getString("investors"), //$NON-NLS-1$
				result.getString("crunchbase_link"), //$NON-NLS-1$
				result.getString("twitter_link"), //$NON-NLS-1$
				result.getString("angel_link"),  //$NON-NLS-1$
				result.getString("linkedin_link"),  //$NON-NLS-1$
				result.getString("blog_link")); //$NON-NLS-1$
		return companyInfo;
	}

	/**
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private InvestorInfo buildIndex4Investors(ResultSet result) throws SQLException {
		final InvestorInfo investorInfo = new InvestorInfo(
				result.getString("investor_name"), //$NON-NLS-1$
				result.getString("investedCompanies"), //$NON-NLS-1$
				result.getString("investorType")); //$NON-NLS-1$
		return investorInfo;
	}
	
	private InvestmentInfo4ESIndexing buildIndex4Investments(ResultSet result) throws SQLException {
		final InvestmentInfo4ESIndexing investmentInfo4ESIndexing = new InvestmentInfo4ESIndexing(
				result.getInt("iid"), //$NON-NLS-1$
				result.getInt("cid"), //$NON-NLS-1$
				result.getString("amount"), //$NON-NLS-1$
				result.getString("round"), //$NON-NLS-1$
				result.getString("investor_type"), //$NON-NLS-1$
				result.getString("year_founded"), //$NON-NLS-1$
				result.getInt("headcount"),  //$NON-NLS-1$
				result.getString("irevenue")); //$NON-NLS-1$
		return investmentInfo4ESIndexing;
	}
	
	public CompanyEmployeesInfo buildIndex4CompanyEmployees(ResultSet result) throws SQLException {
		final CompanyEmployeesInfo companyEmployeesInfo = new CompanyEmployeesInfo(
				result.getInt("eid"), //$NON-NLS-1$
				result.getInt("cid"), //$NON-NLS-1$
				result.getString("designation"), //$NON-NLS-1$
				result.getString("education"), //$NON-NLS-1$
				result.getString("experience"), //$NON-NLS-1$
				result.getString("linkedin"), //$NON-NLS-1$
				result.getString("name"));	 //$NON-NLS-1$
		return companyEmployeesInfo;
	}

	public List<CompanyInfo> getCompanySearchResultsFromIndexedCloudData(JestClient jestClient, String companyName4Search)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyName4Search));//same as QueryBuilders.queryStringQuery("google")
        Search search = new Search.Builder(searchSourceBuilder.toString())
		        .addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<CompanyInfo> companyNames = (List<CompanyInfo>)result.getSourceAsObjectList(CompanyInfo.class);
		//if(companyNames.size()<=0 || companyNames.isEmpty()) return null;
		//else 
		return companyNames;
	}
	
	public List<CompanyEmployeesInfo> getEmployeesSearchResultsFromIndexedCloudData(JestClient jestClient, String companyName4EmployeesSearch)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
		searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyName4EmployeesSearch));//same as QueryBuilders.queryStringQuery("google")
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<CompanyEmployeesInfo> companyEmployeesNames = (List<CompanyEmployeesInfo>)result.getSourceAsObjectList(CompanyEmployeesInfo.class);
		List<CompanyEmployeesInfo> employees = new ArrayList<CompanyEmployeesInfo>();
		CompanyInfo companyOfInterest = (CompanyInfo)getCompanyInfo4SelectedCompanyFromIndexedCloudData(jestClient, companyName4EmployeesSearch);
		if(companyEmployeesNames.size()<=0 || companyEmployeesNames.isEmpty()) return employees;
		else 
		{
			for (int i = 0; i <companyEmployeesNames.size(); i++) 
			{
				if(companyEmployeesNames.get(i).getCid() == companyOfInterest.getCid())
				{
					employees.add(companyEmployeesNames.get(i));
				}
			}
			return employees;
		}
	}
	
	public List<InvestmentInfo4ESIndexing> getInvestmentsSearchResultsFromIndexedCloudData(JestClient jestClient, String companyName4InvestmentsSearch)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
		searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyName4InvestmentsSearch));//same as QueryBuilders.queryStringQuery("google")
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_Investments_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<InvestmentInfo4ESIndexing> investments = (List<InvestmentInfo4ESIndexing>)result.getSourceAsObjectList(InvestmentInfo4ESIndexing.class);
		List<InvestmentInfo4ESIndexing> investments4gvCompany = new ArrayList<InvestmentInfo4ESIndexing>();
		CompanyInfo companyOfInterest = (CompanyInfo)getCompanyInfo4SelectedCompanyFromIndexedCloudData(jestClient, companyName4InvestmentsSearch);
		if(investments.size()<=0 || investments.isEmpty()) return investments4gvCompany;
		else 
		{
			for (int i = 0; i <investments.size(); i++) 
			{
				if(investments.get(i).getCid() == companyOfInterest.getCid())
				{
					investments4gvCompany.add(investments.get(i));
				}
			}
			return investments4gvCompany;
		}
	}
	
	public List<InvestorInfo> getInvestorsSearchResultsFromIndexedCloudData(JestClient jestClient, String companyName4InvestorsSearch)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
		searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyName4InvestorsSearch));//same as QueryBuilders.queryStringQuery("google")
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<InvestorInfo> investors = (List<InvestorInfo>)result.getSourceAsObjectList(InvestorInfo.class);
		List<InvestorInfo> investors4givenCompany = new ArrayList<InvestorInfo>();
		CompanyInfo companyOfInterest = (CompanyInfo)getCompanyInfo4SelectedCompanyFromIndexedCloudData(jestClient, companyName4InvestorsSearch);
		if(investors.size()<=0 || investors.isEmpty()) return investors4givenCompany;
		else 
		{
			for (int i = 0; i <investors.size(); i++) 
			{
				if(investors.get(i).getInvestedCompanies().toLowerCase().contains(companyOfInterest.getName().toLowerCase()))
				{
					investors4givenCompany.add(investors.get(i));
				}
			}
			return investors4givenCompany;
		}
	}
	
	public CompanyInfo getCompanyInfo4SelectedCompanyFromIndexedCloudData(JestClient jestClient, String companyNamestr)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyNamestr));//same as QueryBuilders.queryStringQuery("google")
        Search search = new Search.Builder(searchSourceBuilder.toString())
		        .addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_Companies_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<CompanyInfo> companyNames = (List<CompanyInfo>)result.getSourceAsObjectList(CompanyInfo.class);
		if(companyNames.size()<=0 || companyNames.isEmpty()) 
			return null;
		else
		{
			CompanyInfo company2Return = new CompanyInfo();
			for (int i = 0; i < companyNames.size(); i++) 
			{
				company2Return = companyNames.get(i);
				String compNamestr=companyNames.get(i).getName();
				if(companyNamestr.toLowerCase().equalsIgnoreCase(compNamestr.toLowerCase())) 
					return company2Return;
			}
		}
		return null;
	}

	
	public CompanyEmployeesInfo getCompanyEmployeesInfo4SelectedCompanyFromIndexedCloudData(JestClient jestClient, String companyNamestr)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(companyNamestr));//same as QueryBuilders.queryStringQuery("google")
        Search search = new Search.Builder(searchSourceBuilder.toString())
		        .addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_CompEmployees_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<CompanyEmployeesInfo> companyEmployeesNames = (List<CompanyEmployeesInfo>)result.getSourceAsObjectList(CompanyEmployeesInfo.class);
		if(companyEmployeesNames.size()<=0 || companyEmployeesNames.isEmpty()) 
			return null;
		else
		{
			CompanyEmployeesInfo companyEmployee2Return = new CompanyEmployeesInfo();
			for (int i = 0; i < companyEmployeesNames.size(); i++) 
			{
				companyEmployee2Return = companyEmployeesNames.get(i);
				String compNamestr=companyEmployeesNames.get(i).getName();
				if(companyNamestr.equalsIgnoreCase(compNamestr)) 
					return companyEmployee2Return;
			}
		}
		return null;
	}

	public CompanyInfo[] getUserListCompanyInfosArray(String[] selectedListIDCompanies) {
		JestClient jestClient = getJESTclient4MainIndexInAmazonCloud();
		CompanyInfo[] companiesOfUserListGiven = new CompanyInfo[selectedListIDCompanies.length];
		try {
			for (int i = 0; i < selectedListIDCompanies.length; i++) {
					CompanyInfo company = (CompanyInfo)getCompanyInfo4SelectedCompanyFromIndexedCloudData(jestClient, selectedListIDCompanies[i]);
					companiesOfUserListGiven[i] = company;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return companiesOfUserListGiven;
	}
	
	public InvestorInfo getInvestorInfo4SelectedCompanyInvestorFromIndexedCloudData(JestClient jestClient, String investorName)
			throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();      	        
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(investorName));//same as QueryBuilders.queryStringQuery("google")
        Search search = new Search.Builder(searchSourceBuilder.toString())
		        .addIndex(awsCloud_MAIN_INDEX_NAME).addType(FGDB_Investors_TypeName_FROM_awsCloud_MAIN_INDEX).build();
		JestResult result = jestClient.execute(search);
		List<InvestorInfo> investors = (List<InvestorInfo>)result.getSourceAsObjectList(InvestorInfo.class);
		if(investors.size()<=0 || investors.isEmpty()) 
			return null;
		else
		{
			InvestorInfo investor2Return = new InvestorInfo();
			for (int i = 0; i < investors.size(); i++) 
			{
				investor2Return = investors.get(i);
				String investorNameString=investors.get(i).getInvestor_name();
				if(investorName.equalsIgnoreCase(investorNameString)) 
					return investor2Return;
			}
		}
		return null;
	}

	
}
