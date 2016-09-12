package gov.gsa.dss.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.gsa.controller.RetrieveController;
import gov.gsa.dss.helper.ExceptionHandlerService;

@Path("/retrieve/downloaddocuments")
public class Retrieve {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// 
	public Response downloaddocuments(@QueryParam ("PackageId") String strPackageId)    {

		try{
			RetrieveController obj =new RetrieveController();
			return Response.ok(obj.getZippedDocuments(strPackageId), MediaType.APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			
			//return Response.ok(ehs.parseException(e)+"", MediaType.TEXT_PLAIN).build();
			return Response.status(404).type("text/plain")
	                .entity(ehs.parseException(e)+"").build();
			
		}

	}

}

