package gov.gsa.dss.views.service

import com.fasterxml.jackson.databind.ObjectMapper
import gov.gsa.controller.CreatePackageFromTemplateController
import gov.gsa.dss.helper.ExceptionHandlerService
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
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject

@Path("/dssCreatePackageFromTemplate")
public class CreatePackageFromTemplate {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	Response dssCreatePackageFromTemplate(@Context HttpServletRequest request, String data /* this is the json string*/ ) {

		try {
			HashMap<String,Object> mappedData =
					new ObjectMapper().readValue(data, HashMap.class);
			CreatePackageFromTemplateController createPackageFromTemplateController = new CreatePackageFromTemplateController();
			Map<String, Object> result = createPackageFromTemplateController.dssUniversalConnectorFromTemplate(mappedData);

			/*Convert Map to JSON string*/
			JSONObject jsonResult = new JSONObject();
			jsonResult.putAll( result );
			println "Rest call to createPackageFromTemplate completed."
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
