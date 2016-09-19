package gov.gsa.dss.services;

import java.io.BufferedReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import gov.gsa.controller.AlfrescoController;
import gov.gsa.controller.RetrieveController;


@Path("/alfresco")

public class IacpPackageComplete {
	/**
	 * Class retrieve
	 */
	
	
	@Path("zipFileUpload")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// 
	/**
	 * 
	 * @param strPackageId
	 * @param strOrgName
	 * @return Application JSON (Base64 encoded zipped docs+ evidence summary) and Package name
	 */
	public Response downloaddocuments(@QueryParam ("PackageId") String strPackageId, @QueryParam ("orgName") String strOrgName)    {
		AlfrescoController obj =new AlfrescoController();

		return obj.uploadPackagetoEDMS(strPackageId, strOrgName);
		

	}

}