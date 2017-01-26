package gov.gsa.dss.views.integration

import com.silanis.esl.sdk.DocumentPackage

import gov.gsa.controller.RetrieveController;
import gov.gsa.dss.helper.DSSQueueManagement
import gov.gsa.dss.helper.SignerInfo
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject
import org.apache.log4j.Logger;

/**
 * Created by Sudhangi on 8/24/2016.
 */
class RetaCallbackHandler {
	final static Logger log =Logger.getLogger(RetaCallbackHandler.class);

	/**
	 * Structures messges for submission to RETA Queue
	 * @param eventOccurred
	 * @param packageId
	 * @param packageName
	 * @param orgName
	 * @param documentPackage
	 */
	public void retaPublishToQueue(String eventOccurred, String packageId , String packageName, String orgName, DocumentPackage documentPackage) {

		Map<String, String> myMessage = new HashMap<String, String>();
		Map<String, String> packageDetails = new HashMap<String, String>();

		switch (eventOccurred) {
			case "PACKAGE_COMPLETE":
				SignerInfo packageSigners = new SignerInfo();
				myMessage.put("orgName", orgName);
				myMessage.put("notificationType", eventOccurred);
				packageDetails.put("packageId", packageId);
				packageDetails.put("packageName", packageName);
				packageDetails.put("signers",packageSigners.getSigners(packageId) )
				myMessage.put("packageInfo", packageDetails);
				break;
			//Package Decline option shall be removed. to be deleted
			/*			case "PACKAGE_DECLINE":
			 //String declineReason = documentPackage.getMessages().get(0).getContent();
			 PackageSigner packageDecliners= new PackageSigner();
			 myMessage.put("orgName", orgName);
			 myMessage.put("notificationType", eventOccurred);
			 packageDetails.put("packageId", packageId);
			 packageDetails.put("packageName", packageName);
			 //packageDetails.put("declineReason",declineReason);
			 packageDetails.put("decliner",packageDecliners.getDecliners(packageId))
			 myMessage.put("packageInfo", packageDetails);
			 break;*/
			case "PACKAGE_OPT_OUT":
			//String declineReason = documentPackage.getMessages().get(0).getContent();
				SignerInfo packageOptOut= new SignerInfo();
				myMessage.put("orgName", orgName);
				myMessage.put("notificationType", eventOccurred);
				packageDetails.put("packageId", packageId);
				packageDetails.put("packageName", packageName);
			//packageDetails.put("declineReason",declineReason);
				packageDetails.put("opted_out_by",packageOptOut.getOptOutDetails(packageId));
				myMessage.put("packageInfo", packageDetails);
				break;
		}

		JSONObject jsonResult = new JSONObject();
		jsonResult.putAll( myMessage );
		def messageString = jsonResult.toString();

		DSSQueueManagement dssQueue = new DSSQueueManagement()

		dssQueue.initConnection()

		def result = dssQueue.publishToQueue("DSS_RETA_QUEUE_DEV", messageString);
		log.info ("published to queue: " + result);

		dssQueue.closeConnection()
	}
	
	/**
	 * Structures messages for submission to EDMS Queue
	 * @param eventOccurred
	 * @param packageId
	 * @param packageName
	 * @param orgName
	 * @param documentPackage
	 */
	public void edmsMessageBuilder(String eventOccurred, String packageId , String packageName, String orgName, DocumentPackage documentPackage)
	{
		Map<String, String> myMessage = new HashMap<String, String>();
		Map<String, String> packageDetails = new HashMap<String, String>();

		switch (eventOccurred) {
			case "PACKAGE_COMPLETE":
				SignerInfo packageSigners = new SignerInfo();
				myMessage.put("orgName", orgName);
				myMessage.put("notificationType", eventOccurred);
				myMessage.put("packageId", packageId);
				myMessage.put("packageInfo", packageDetails);
				break;
		}
		JSONObject jsonResult = new JSONObject();
		jsonResult.putAll( myMessage );
		def messageString = jsonResult.toString();

		DSSQueueManagement dssQueue = new DSSQueueManagement()

		dssQueue.initConnection()

		def result = dssQueue.publishToQueue("DSS_EDMS_QUEUE_DEV", messageString);
		log.info ("published to queue: " + result);

		dssQueue.closeConnection()
		
	}

}
