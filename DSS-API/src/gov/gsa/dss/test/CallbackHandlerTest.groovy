package gov.gsa.dss.test;
import static org.junit.Assert.*

import java.util.HashMap;

import gov.gsa.controller.CallbackHandlerController
import javax.naming.InitialContext
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class CallbackHandlerTest {
	
	/**
	 *
	 * @throws InterruptedException
	 * This is the success path with a valid Package ID
	 */
	
	/*This initializes the map object*/
	public HashMap<String,Object> testInit() {
		HashMap<String,Object> initMappedData = new HashMap<String, Object>();
		initMappedData.putAt("@class","com.silanis.esl.packages.event.ESLProcessEvent");
		initMappedData.putAt("name","PACKAGE_CREATE")
		initMappedData.putAt("sessionUser","cxucq1b8ZzsC")
		initMappedData.putAt("message","null")
		initMappedData.putAt("documentId","null")
		return initMappedData;
	}
	
	/*This is a successful path test case with correct package id*/
	@Test
	public void testSuccessCallbackRouting() throws InterruptedException{

		String data = "{\"@class\":\"com.silanis.esl.packages.event.ESLProcessEvent\",\"name\":\"PACKAGE_CREATE\",\"sessionUser\":\"cxucq1b8ZzsC\",\"packageId\":\"70fcca8f-dfb3-4f69-aa3d-e6021c709cc6\",\"message\":null,\"documentId\":null}\"";
		CallbackHandlerController callbackController = new CallbackHandlerController();
		String sEvent = "PACKAGE_COMPLETE";
		HashMap<String,Object> mappedData = new HashMap<String, Object>();
		mappedData = testInit();
		mappedData.putAt("packageId","70fcca8f-dfb3-4f69-aa3d-e6021c709cc6")
		Response response = callbackController.routeCallback(mappedData, sEvent);
		println response;

		assertEquals("Should return status 200", 200, response.getStatus());

		response.getEntity();
		response.close();
	}
	
	/*This is a test case where the orgName for the package does not exits*/
	@Test
	public void testDefaultsCallbackRouting() throws InterruptedException{
		
				String data = "{\"@class\":\"com.silanis.esl.packages.event.ESLProcessEvent\",\"name\":\"PACKAGE_CREATE\",\"sessionUser\":\"cxucq1b8ZzsC\",\"packageId\":\"70fcca8f-dfb3-4f69-aa3d-e6021c709cc6\",\"message\":null,\"documentId\":null}\"";
				CallbackHandlerController callbackController = new CallbackHandlerController();
				String sEvent = "PACKAGE_COMPLETE";
				HashMap<String,Object> mappedData = new HashMap<String, Object>();
				mappedData = testInit();
				mappedData.putAt("packageId","b8f6343f-ec58-4789-996b-60cfd75c4df1")
				
				Response response = callbackController.routeCallback(mappedData, sEvent);
				println response;
		
				assertEquals("Should return status 200", 200, response.getStatus());
		
				response.getEntity();
				response.close();
			}
	
	/*This is an exception test case where the package does not exits for the given package-id*/
	@Test
	public void testExceptionCallbackRouting() throws InterruptedException{
		
				String data = "{\"@class\":\"com.silanis.esl.packages.event.ESLProcessEvent\",\"name\":\"PACKAGE_CREATE\",\"sessionUser\":\"cxucq1b8ZzsC\",\"packageId\":\"70fcca8f-dfb3-4f69-aa3d-e6021c709cc6\",\"message\":null,\"documentId\":null}\"";
				CallbackHandlerController callbackController = new CallbackHandlerController();
				String sEvent = "PACKAGE_COMPLETE";
				HashMap<String,Object> mappedData = new HashMap<String, Object>();
				mappedData = testInit();
				mappedData.putAt("packageId","70fcca8f-dfb3-4f69-aa3d-e6091c709cc6")
				println "Exception.............."
				println mappedData;
				
				Response response = callbackController.routeCallback(mappedData, sEvent);
				println response;
		
				assertEquals("Should return status 404", 404, response.getStatus());
		
				response.getEntity();
				response.close();
			}


}
