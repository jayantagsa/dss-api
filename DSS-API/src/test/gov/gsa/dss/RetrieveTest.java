package test.gov.gsa.dss;
import static org.junit.Assert.*;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class RetrieveTest {
	
	/**
	 * 
	 * @throws InterruptedException
	 * This is the success path with a valid Package ID with Matching Package name
	 */
	@Test
	public void testSuccessRetrieveDownloadZippedDocs() throws InterruptedException{

		
		Client client = ClientBuilder.newClient();
		WebTarget ret =client.target("http://localhost:8080/DSS-API/dss/retrieve/downloadDocuments?packageId=af5a31d6-9c0d-4367-bd93-3561c0d755b8&orgName=RETA"); //...;
		Response response = ret.request(MediaType.APPLICATION_JSON).get();

		// response.readEntity(Map.class);
		assertEquals("Should return status 200", 200, response.getStatus());

		response.getEntity();
		response.close();

	}
	
	/**
	 * This is a failure path with a invalid/empty Package ID
	 * @throws InterruptedException
	 */
	@Test
	public void testFailureWrongPackageRetrieveDownloadZippedDocs() throws InterruptedException{

		Client client = ClientBuilder.newClient();
		WebTarget ret =client.target("http://localhost:8080/DSS-API/dss/retrieve/downloadDocuments?packageId=af5a31d6-9c0d-4367-bd93-&orgName=RETA"); //...;
		Response response = ret.request(MediaType.APPLICATION_JSON).get();
		
		
		// response.readEntity(Map.class);
		assertEquals("Should return status 404", 404, response.getStatus());
		//String output = (String)

		response.close();

	}
/**
 * THis is failure Path with invalid Invalid orgname
 * @throws InterruptedException
 */
	@Test
	public void testFailureWrongOrgDownloadZippedDocs() throws InterruptedException{

		Client client = ClientBuilder.newClient();
		WebTarget ret =client.target("http://localhost:8080/DSS-API/dss/retrieve/downloadDocuments?packageId=af5a31d6-9c0d-4367-bd93-3561c0d755b8&orgName=BETA"); //...;
		Response response = ret.request(MediaType.APPLICATION_JSON).get();

		// response.readEntity(Map.class);
		assertEquals("Should return status 551", 551, response.getStatus());


		response.close();


	}
	
	/**
	 * this is failure path with a valid PackageID and orgname but the orgName doesn't match the orgname of the package
	 */
/*	@Test
	public void testFailureCorrectPackageWrongOrgDownloadZippedDocs() throws InterruptedException{

		Client client = ClientBuilder.newClient();
		WebTarget ret =client.target("http://localhost:8080/DSS-API/dss/retrieve/downloaddocuments?PackageId=http://localhost:8080/DSS-API/dss/retrieve/downloaddocuments?PackageId=46562836-566b-454b-90e4-c6e99f3f2314&orgName=BETA"); //...;
		Response response = ret.request(MediaType.APPLICATION_JSON).get();

		// response.readEntity(Map.class);
		assertEquals("Should return status 550", 550, response.getStatus());


		response.close();


	}*/

}
