package gov.gsa.dss.test
import static org.junit.Assert.*

import java.util.HashMap;
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import gov.gsa.controller.CallbackHandlerController
import gov.gsa.controller.CreatePackageController
import javax.naming.InitialContext
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

class CreatePackageTest {

	/**
	 *
	 * @throws InterruptedException
	 * This is the success path with a valid Package ID
	 */

	/*This initializes the map object*/
	public HashMap<String,Object> testInit() {
		InputStream inputStream1 = this.getClass().getResourceAsStream("TEST-AAAP-Short.PDF");
		InputStream inputStream2 = this.getClass().getResourceAsStream("TEST-CDT-Short.pdf");

		//        encode the inputstream into base64 string
		def bytes = IOUtils.toByteArray(inputStream1);
		def bytes64 = Base64.encodeBase64(bytes);
		def content1 = new String(bytes64);

		bytes = IOUtils.toByteArray(inputStream2);
		bytes64 = Base64.encodeBase64(bytes);
		def content2 = new String(bytes64);


		def signerMap1 = [signerEmail      : "dssdeveloper.sudhangi@gmail.com",
			signerFirstName  : "Dss",
			signerLastName   : "Developer",
			searchText       : "",
			signType         : "ClickToInitial",
			signaturePosition: "TOPRIGHT"]
		def signerMap2 = [signerEmail      : "dssdev11@gmail.com",
			signerFirstName  : "dss",
			signerLastName   : "One",
			searchText       : "GOVERNMENT",
			signType         : "CaptureSignature",
			signaturePosition: "BOTTOMRIGHT"]
		def signers1 = [signerMap1, signerMap2]

		signerMap1 = [signerEmail      : "dssdev12@gmail.com",
			signerFirstName  : "Dss",
			signerLastName   : "Two",
			searchText       : "TextCDT",
			signType         : "ClickToInitial",
			signaturePosition: "TOPRIGHT",
			noteToSigner	 : "Please sign this document as soon as possible. This is high priority."]
		signerMap2 = [signerEmail      : "dssdev13@gmail.com",
			signerFirstName  : "Dss",
			signerLastName   : "Three",
			searchText       : "StringCDT",
			signType         : "CaptureSignature",
			signaturePosition: "BOTTOMRigHT"]
		def signers2 = [signerMap1, signerMap2]

		def documentMap1 = [documentName: "AAAP.PDF", documentContent: content1, signers: signers1]
		def documentMap2 = [documentName: "CDT.PDF", documentContent: content2, signers: signers2]

		def documents1 = [document: documentMap1]
		def documents2 = [document: documentMap2]
		def documents = [documents1, documents2]
		/**
		 * The code returns the following HashMap<String,Object>.
		 */
		def allData = [orgName           : "AAAP",
			senderEmail       : "sudhangi.ambekar@icfi.com",
			packageName       : "UnitTestDssUnivConn",
			enableSigningOrder: false,
			packageOption     : "create",
			packageDescription: "This is a high priority package.",
			documents         : documents]
		return allData;
	}

	/*This is a successful path test case with correct data being sent over to create package*/
	@Test
	void testDssUniversalConnector() {
		def allData = testInit();
		CreatePackageController createPackageController = new CreatePackageController();
		def result = createPackageController.dssUniversalConnector(allData);
		assert result != null
//		        System.out.println("result:" + result)
	}
	
	/**
	 * This is a error test case with wrong text search. 
	 * Error code 563 is validated in the actual dssUniversalConnecor() Code so it needs to be checked here and not in the testValidateData() test case
	 * */
	@Test
	void testDssUniversalConnectorError() {
		def allData = testInit();
		HashMap<String,Object> exceptedMap = new HashMap<String, Object>();
		
		exceptedMap.putAt("code","563");
		exceptedMap.putAt("message","Search Text not found in the document.")
		exceptedMap.putAt("type","Validation Error")
		CreatePackageController createPackageController = new CreatePackageController();
		def result = createPackageController.dssUniversalConnector(allData);
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
		CreatePackageController createPackageController = new CreatePackageController();
		def result = createPackageController.validateData(allData);
		assert result == null
		//        System.out.println("Result for Test 1:" + result)

		/** Test 2
		 * This will return an error because it is the 'Sender email incorrect format' test
		 */
		allData.putAt("senderEmail", "tes@@@***.com")
		result = createPackageController.validateData(allData);
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
		result = createPackageController.validateData(allData);
			exceptedMap.putAt("code","540");
			exceptedMap.putAt("message","Package Name not provided or is empty.")
			exceptedMap.putAt("type","Validation Error.")
		assertEquals(exceptedMap, result)
//		System.out.println("Result for Test 2:" + result)

	}
	
}

