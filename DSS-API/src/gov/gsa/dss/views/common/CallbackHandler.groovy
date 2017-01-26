package gov.gsa.dss.views.common;

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
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.Validator;
import org.owasp.esapi.ESAPI;


@Path ("/executeCallbackHandler")
public class CallbackHandler {
	//@Path("")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//
	/**
	 * 
	 * @param request HTTP Servlet Request
	 * @param sEvent The json string which has details about the event that occurred
	 * @return Response to the call
	 */
	Response executeCallbackHandler(
			@Context HttpServletRequest request, String sEvent) {
		Validator validator = ESAPI.validator();
		ValidationErrorList errorList = new ValidationErrorList();
		String validatedSEvent =null;

		if (validator.isValidInput("sEvent", sEvent, "HTTPParameterValue",sEvent.length() ,false, errorList)){
			validatedSEvent=sEvent;
		}
		HashMap<String,Object> mappedData =
				new ObjectMapper().readValue(sEvent, HashMap.class);

		CallbackHandlerController callbackHandlerController = new CallbackHandlerController();
		callbackHandlerController.routeCallback(mappedData, validatedSEvent);
		return Response.ok("{\"code\":\"success\"}", MediaType.APPLICATION_JSON).build();
	}

}

