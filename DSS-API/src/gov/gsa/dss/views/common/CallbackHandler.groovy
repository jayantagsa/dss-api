package gov.gsa.dss.views.common;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.*


import com.fasterxml.jackson.databind.ObjectMapper
import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId
import gov.gsa.controller.CallbackHandlerController
import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import org.apache.commons.lang3.StringUtils


@Path ("/executeCallbackHandler")
public class CallbackHandler {
	//@Path("")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//
	void executeCallbackHandler(
		
			@Context HttpServletRequest request, String sEvent) {
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");;//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
			//println();
			//println("Start===========Logs for eSignLive");
			//println ("received package_complete notification from esignlive at:\t"+ sdfDate.format(now));
			long startTime = System.currentTimeMillis();
		HashMap<String,Object> mappedData =
				new ObjectMapper().readValue(sEvent, HashMap.class);
		println(mappedData);
		println(sEvent);
		CallbackHandlerController callbackHandlerController = new CallbackHandlerController();
		callbackHandlerController.routeCallback(mappedData, sEvent);	
		Date endnow = new Date();
		 strDate = sdfDate.format(endnow);
		//println ("received package_complete notification from esignlive at:\t"+ strDate);
		//println("End===========Logs for eSignLive");
		}
			
}

