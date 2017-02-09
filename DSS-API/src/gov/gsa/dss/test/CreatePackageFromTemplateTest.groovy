package gov.gsa.dss.test
import static org.junit.Assert.*

import java.util.HashMap;
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import gov.gsa.dss.controller.CallbackHandlerController
import gov.gsa.dss.controller.CreatePackageFromTemplateController;

import javax.naming.InitialContext
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

class CreatePackageFromTemplateTest {

	/**
	 *
	 * @throws InterruptedException
	 * This is the success path with a valid Package ID
	 */

	/*This initializes the map object*/
	public HashMap<String,Object> testInit() {
		

		def signerMap1 = [signerEmail      : "dssdeveloper.sudhangi@gmail.com",
			signerFirstName  : "Dss",
			signerLastName   : "Developer",
			"placeHolder": "Signer1",
    		"noteToSigner": "Please sign this asap."]
		def signerMap2 = [signerEmail      : "dssdev11@gmail.com",
			signerFirstName  : "dss",
			signerLastName   : "One",
			"placeHolder": "Signer2",
    		"noteToSigner": "Please sign this asap."]
		def signers1 = [signerMap1, signerMap2]


		/**
		 * The code returns the following HashMap<String,Object>.
		 */
		def allData = [orgName           : "AAAP",
			senderEmail       : "sudhangi.ambekar@icfi.com",
			packageName       : "UnitTestDssUnivConn",
			packageOption     : "create",
			packageDescription: "This is a high priority package.",
			templateId			: "4a2a65bb-23a6-4362-a35f-a82850037aa9",	
			signers         : signers1]
		return allData;
	}

	/*This is a successful path test case with correct data being sent over to create package*/
	@Test
	void testDssUniversalConnectorFromTemplate() {
		def allData = testInit();
		CreatePackageFromTemplateController createPackageFromTemplateController = new CreatePackageFromTemplateController();
		def result = createPackageFromTemplateController.dssUniversalConnectorFromTemplate(allData);
		assert result != null
//		        System.out.println("result:" + result)
	}
	
	/**
	 * This is a error test case with wrong text search. 
	 * Error code 564 is validated in the actual dssUniversalConnectorFromTemplate() Code so it needs to be checked here and not in the testValidateData() test case
	 * */
	@Test
	void testDssUniversalConnectorFromTemplateError() {
		
		def signerMap1 = [signerEmail      : "dssdeveloper.sudhangi@gmail.com",
			signerFirstName  : "Dss",
			signerLastName   : "Developer",
			"placeHolder": "Signer1",
    		"noteToSigner": "Please sign this asap."]
		
		def signers1 = [signerMap1]

		def allData = [orgName           : "AAAP",
			senderEmail       : "sudhangi.ambekar@icfi.com",
			packageName       : "UnitTestDssUnivConn",
			packageOption     : "create",
			packageDescription: "This is a high priority package.",
			templateId			: "4a2a65bb-23a6-4362-a35f-a82850037aa9",
			signers         : signers1]
		HashMap<String,Object> exceptedMap = new HashMap<String, Object>();
		
		exceptedMap.putAt("code","564");
		exceptedMap.putAt("message","Number of signers provided do not match the number of signature placeholders in the template.")
		exceptedMap.putAt("type","Validation Error.")
		CreatePackageFromTemplateController createPackageFromTemplateController = new CreatePackageFromTemplateController();
		def result = createPackageFromTemplateController.dssUniversalConnectorFromTemplate(allData);
		assert result != null
		assertEquals(exceptedMap, result)
		
		        System.out.println("result:" + result)
	}
	
	@Test
	void testValidateData() {
		def allData = testInit();
		HashMap<String,Object> exceptedMap = new HashMap<String, Object>();

		/** Test 1
		 * This is a success test case when all the data that is sent is valid.
		 */
		CreatePackageFromTemplateController createPackageFromTemplateController = new CreatePackageFromTemplateController();
		def result = createPackageFromTemplateController.validateData(allData);
		assert result == null
		//        System.out.println("Result for Test 1:" + result)

		/** Test 2
		 * This will return an error because it is the 'Sender email incorrect format' test
		 */
		allData.putAt("senderEmail", "tes@@@***.com")
		result = createPackageFromTemplateController.validateData(allData);
			exceptedMap.putAt("code","542");
			exceptedMap.putAt("message","Sender email address not provided or is invalid.")
			exceptedMap.putAt("type","Validation Error.")
		assertEquals(exceptedMap, result)
//		System.out.println("Result for Test 2:" + result)
		
		/** Test 3
		 * This will return an error because it is the 'Package Name missing' test
		 */
		allData.putAt("senderEmail", "test1@gmail.com")
		allData.putAt("packageName", "")
		result = createPackageFromTemplateController.validateData(allData);
			exceptedMap.putAt("code","540");
			exceptedMap.putAt("message","Package Name not provided or is empty.")
			exceptedMap.putAt("type","Validation Error.")
		assertEquals(exceptedMap, result)
//		System.out.println("Result for Test 3:" + result)

	}
	
}

