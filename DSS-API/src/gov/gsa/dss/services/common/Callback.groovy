package gov.gsa.dss.services.common;

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
import gov.gsa.controller.CallbackController
import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import org.apache.commons.lang3.StringUtils


@Path ("/executeCallbackHandler")
public class Callback {
	//@Path("")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//
	void executeCallbackHandler(
			@Context HttpServletRequest request, String sEvent) {

		HashMap<String,Object> mappedData =
				new ObjectMapper().readValue(sEvent, HashMap.class);
		
		CallbackController callbackController = new CallbackController();
		callbackController.routeCallback(mappedData, sEvent);		
		}
			
}

