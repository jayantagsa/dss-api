package gov.gsa.dss.views.service;

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
import gov.gsa.controller.CreatePackageController;
import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject
import org.apache.commons.lang3.StringUtils


@Path("/dssUniversalConnector")
public class CreatePackage {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//
	Response dssUniversalConnector(@Context HttpServletRequest request, String data /* this is the json string*/ ) {

		try {
			HashMap<String,Object> mappedData =
					new ObjectMapper().readValue(data, HashMap.class);
			CreatePackageController createPackageController = new CreatePackageController();
			Map<String, Object> result = createPackageController.dssUniversalConnector(mappedData);
			/*Convert Map to JSON string*/
			JSONObject jsonResult = new JSONObject();
			jsonResult.putAll( result );
			println "Rest call to dssUniversalConnector completed."
			return Response.ok(jsonResult.toString(), MediaType.APPLICATION_JSON).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			String msg = ehs.parseException(e)+"";
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type("text/plain")
					.entity(ehs.parseException(e)+"").build();
		}
	}

}

