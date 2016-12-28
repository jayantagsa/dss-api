package gov.gsa.dss.views.integration;

import java.io.BufferedReader;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.ClientConfig;

import gov.gsa.controller.IACPPackageController;




@Path("/edms")

public class IacpPackage {
	
    @Context
    UriInfo uriInfo;
	
	@Path("uploadACPZip")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// 
	/**
	 * 
	 * @param strPackageId
	 * @param strOrgName
	 * @return Application JSON (Base64 encoded zipped docs+ evidence summary) and Package name
	 */
	public Response uploadEDMS(@QueryParam ("packageId") String strPackageId, @QueryParam ("orgName") String strOrgName) throws IOException    {
		System.out.println(uriInfo.getBaseUri());
		
		IACPPackageController obj =new IACPPackageController();

		return obj.uploadPackagetoEDMS(strPackageId, strOrgName);
		

	}

}