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
import gov.gsa.dss.helper.PackageOrgName;
import gov.gsa.dss.views.integration.RetaCallbackHandler;

class CallbackHandlerController {


	public Response routeCallback (HashMap<String,Object> mappedData, String sEvent){

		try {

			Authenticator auth = new Authenticator();
			EslClient dssEslClient = auth.getAuth();
			EmailContent emailContent = new EmailContent();
			RetaCallbackHandler retaCallbackHandler = new RetaCallbackHandler();
			IACPPackageController iacpPackageController = new IACPPackageController();
			PackageOrgName packageOrgName = new PackageOrgName();
			
			def eventOccurred = mappedData.getAt("name");
			String packageIdString = mappedData.getAt("packageId");
			PackageId packageId = new PackageId(packageIdString)
			DocumentPackage documentPackage = dssEslClient.getPackage(packageId);
			def orgName = packageOrgName.getOrgName(documentPackage);
			String packageName = documentPackage.getName();

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
					iacpPackageController.uploadPackagetoEDMS(packageIdString,orgName)
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
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type("text/plain")
					.entity(ehs.parseException(e)+"").build();

		}
	}

}