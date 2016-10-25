package gov.gsa.dss.views.service

import com.fasterxml.jackson.databind.ObjectMapper
import gov.gsa.controller.CreatePackageFromDocLayoutController
import gov.gsa.controller.CreatePackageFromTemplateController
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

@Path("/dssCreatePackageFromDocLayout")
public class CreatePackageFromDocLayout {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	Response dssCreatePackageFromDocLayout(@Context HttpServletRequest request, String data /* this is the json string*/ ) {

		HashMap<String,Object> mappedData =
				new ObjectMapper().readValue(data, HashMap.class);
		CreatePackageFromDocLayoutController createPackageFromDocLayoutController = new CreatePackageFromDocLayoutController();
		Map<String, Object> result = createPackageFromDocLayoutController.dssUniversalConnectorFromDocLayout(mappedData);

		/*Convert Map to JSON string*/
		JSONObject jsonResult = new JSONObject();
		jsonResult.putAll( result );
		println "Rest call to createPackageFromDocLayout completed."

		return Response.ok(jsonResult.toString(), MediaType.APPLICATION_JSON).build();
	}
}