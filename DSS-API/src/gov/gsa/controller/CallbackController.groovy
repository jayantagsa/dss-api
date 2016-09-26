package gov.gsa.controller

import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils
import com.silanis.esl.sdk.EslClient;
import gov.gsa.dss.helper.Authenticator
import gov.gsa.dss.helper.EmailContent;
import gov.gsa.dss.helper.ExceptionHandlerService
import gov.gsa.dss.services.integration.RetaCallbackHandler;;;;

class CallbackController {


	public Response routeCallback (HashMap<String,Object> mappedData, String sEvent){

		try {

			Authenticator auth = new Authenticator();
			EslClient dssEslClient = auth.getAuth();
			EmailContent emailContent = new EmailContent();
			RetaCallbackHandler retaCallbackHandler = new RetaCallbackHandler();

			def eventOccurred = mappedData.getAt("name");
			println "Step 2";
			String packageIdString = mappedData.getAt("packageId");
			println "Step 3";
			PackageId packageId = new PackageId(packageIdString)
			DocumentPackage documentPackage = dssEslClient.getPackage(packageId);
			DocumentPackageAttributes documentPackageAttributes = documentPackage.getAttributes();
			String packageName = documentPackage.getName();
			println "Step 4";
			def orgName = documentPackageAttributes.getContents().get("orgName").toString();
			if ((StringUtils.isEmpty(orgName)) || (orgName == "null")) {
				if (documentPackage.getName().contains('ACP')) {
					orgName="IACP";
				}
			}
			println "Step 5";

			switch (orgName) {
				/*TSP code has been commented out since it is now out of scope.
				 case "TSP":
				 println "tsp event: $eventOccurred"
				 tspCallbackHandler.handleCallback(sEvent)
				 break*/
				case "RETA":
					println "RETA event: $eventOccurred"
					println "Package Name: $packageName"
					println "Package Id: $packageIdString"
				retaCallbackHandler.retaPublishToQueue(eventOccurred, packageIdString, packageName, orgName, documentPackage);
				/*This genericCallbackHandler will not be used for RETA case.*/
				/*genericCallbackHandler.handleCallback(sEvent)*/
					break
				case "IACP":
					println "IACP event: $eventOccurred"
					println "Package Name: $packageName"
					println "Package Id: $packageIdString"
				//iacpCallbackHandler.handleCallback(sEvent)
					break
				default:
					println "Default event occurred: $eventOccurred"
					println "Package Name: $packageName"
					println "Package Id: $packageIdString"
					emailContent.emailOnRoutingExp(eventOccurred, packageName, packageIdString);
					break
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerService ehs = new ExceptionHandlerService();

			//return Response.ok(ehs.parseException(e)+"", MediaType.TEXT_PLAIN).build();
			String msg = ehs.parseException(e)+"";
			;
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type("text/plain")
					.entity(ehs.parseException(e)+"").build();

		}
	}

}