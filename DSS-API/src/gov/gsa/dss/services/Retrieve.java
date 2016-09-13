package gov.gsa.dss.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.gsa.controller.RetrieveController;
import gov.gsa.dss.helper.ExceptionHandlerService;

@Path("/retrieve")
public class Retrieve {
	@Path("downloaddocuments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// 
	public Response downloaddocuments(@QueryParam ("PackageId") String strPackageId, @QueryParam ("orgName") String strOrgName)    {
System.out.println();
		
			RetrieveController obj =new RetrieveController();
					
		return obj.getZippedDocuments(strPackageId,strOrgName);

	}

}

