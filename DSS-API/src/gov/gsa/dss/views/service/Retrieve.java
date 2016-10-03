package gov.gsa.dss.views.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.gsa.controller.RetrieveController;

@Path("/package")

public class Retrieve {
	/**
	 * Class retrieve
	 */
	
	
	@Path("retrieve")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	
	/**
	 * 
	 * @param strPackageId
	 * @param strOrgName
	 * @return Application JSON (Base64 encoded zipped docs+ evidence summary) and Package name
	 */
	public Response downloadDocuments(@QueryParam ("packageId") String strPackageId, @QueryParam ("orgName") String strOrgName)    {
		System.out.println();

		RetrieveController obj =new RetrieveController();

		return obj.getZippedDocuments(strPackageId,strOrgName);

	}

}

