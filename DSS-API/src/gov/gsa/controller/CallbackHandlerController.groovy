package gov.gsa.controller

import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger;

import com.silanis.esl.sdk.EslClient;
import gov.gsa.dss.helper.Authenticator
import gov.gsa.dss.helper.EmailContent;
import gov.gsa.dss.helper.ExceptionHandlerService
import gov.gsa.dss.helper.PackageOrgName;
import gov.gsa.dss.views.integration.RetaCallbackHandler;

class CallbackHandlerController {

	final static Logger log =Logger.getLogger(CallbackHandlerController.class);
	public Response routeCallback (HashMap<String,Object> mappedData, String sEvent){

		try {

			Authenticator auth = new Authenticator();
			EslClient dssEslClient = auth.getAuth();
			EmailContent emailContent = new EmailContent();
			RetaCallbackHandler retaCallbackHandler = new RetaCallbackHandler();
			EDMSController edmsController = new EDMSController();
			PackageOrgName packageOrgName = new PackageOrgName();
			
			def eventOccurred = mappedData.getAt("name");
			String packageIdString = mappedData.getAt("packageId");
			PackageId packageId = new PackageId(packageIdString)
			DocumentPackage documentPackage = dssEslClient.getPackage(packageId);
			def orgName = packageOrgName.getOrgName(documentPackage);
			log.info(orgName);
			String packageName = documentPackage.getName();

			switch (orgName) {
				/*TSP code has been commented out since it is now out of scope.
				 case "TSP":
				 println "tsp event: $eventOccurred"
				 tspCallbackHandler.handleCallback(sEvent)
				 break*/
				case "RETA":
					log.info( "RETA event: $eventOccurred");
					log.info( "Package Name: $packageName");
					log.info( "Package Id: $packageIdString");
					retaCallbackHandler.retaPublishToQueue(eventOccurred, packageIdString, packageName, orgName, documentPackage);
				/*This genericCallbackHandler will not be used for RETA case.*/
				/*genericCallbackHandler.handleCallback(sEvent)*/
					break
				case "IACP":
					log.info( "IACP event: $eventOccurred");
					log.info( "Package Name: $packageName");
					log.info( "Package Id: $packageIdString");
					//edmsController.uploadPackagetoEDMS(packageIdString,orgName)
					//iacpCallbackHandler.handleCallback(sEvent)
					break
				default:
					log.info( "Default event occurred: $eventOccurred");
					log.info( "Package Name: $packageName");
					log.info( "Package Id: $packageIdString");
					emailContent.emailOnRoutingExp(eventOccurred, packageName, packageIdString);
					break
			}
			
			if (eventOccurred=="PACKAGE_COMPLETE" ) {
				edmsController.uploadPackagetoEDMS(packageIdString,orgName)
			}
		}
		catch (Exception e) {
			log.error(e);
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			String msg = ehs.parseException(e)+"";
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type("text/plain")
					.entity(ehs.parseException(e)+"").build();

		}
	}

}