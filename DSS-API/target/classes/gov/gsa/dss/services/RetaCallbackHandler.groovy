package gov.gsa.dss.services

import com.silanis.esl.sdk.DocumentPackage
import gov.gsa.dss.helper.DSSQueueManagement
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject

/**
 * Created by Sudhangi on 8/24/2016.
 */
class RetaCallbackHandler {

    public void retaPublishToQueue(String eventOccurred, String packageId , String packageName, String orgName, DocumentPackage documentPackage) {

        Map<String, String> myMessage = new HashMap<String, String>();
        Map<String, String> packageDetails = new HashMap<String, String>();

        switch (eventOccurred) {
            case "PACKAGE_COMPLETE":
                myMessage.put("orgName", orgName);
                myMessage.put("notificationType", eventOccurred);
                packageDetails.put("packageId", packageId);
                packageDetails.put("packageName", packageName);
                myMessage.put("packageInfo", packageDetails);
                break;
            case "PACKAGE_DECLINE":
                String declineReason = documentPackage.getMessages().get(0).getContent();
                myMessage.put("orgName", orgName);
                myMessage.put("notificationType", eventOccurred);
                packageDetails.put("packageId", packageId);
                packageDetails.put("packageName", packageName);
                packageDetails.put("declineReason",declineReason);
                myMessage.put("packageInfo", packageDetails);
                break;
        }

        JSONObject jsonResult = new JSONObject();
        jsonResult.putAll( myMessage );
        def messageString = jsonResult.toString();

        DSSQueueManagement dssQueue = new DSSQueueManagement()

        dssQueue.initConnection()

        def result = dssQueue.publishToQueue("DSS_RETA_QUEUE_DEV", messageString);
        System.out.println ("published to queue: " + result);

        dssQueue.closeConnection()
    }

}
