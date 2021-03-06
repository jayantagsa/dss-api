package gov.gsa.dss.views.integration;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.Validator;

import gov.gsa.dss.controller.IACPPackageController;
import gov.gsa.dss.helper.staic.OrgCodes;

@Path("/edms")

public class IacpPackage {
	
    @Context
    UriInfo uriInfo;
    final static Logger log =Logger.getLogger(IacpPackage.class);
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
		log.info(uriInfo.getBaseUri());
		Validator validator = ESAPI.validator();
		 ValidationErrorList errorList = new ValidationErrorList();
		 String validatedPackageID =null;
		 String validatedOrgName =null;
		 if (validator.isValidInput("OrgName", strOrgName, "HTTPParameterValue",64 ,false, errorList)){
			 validatedOrgName=strOrgName;
			 OrgCodes.getOrg(validatedOrgName);
		 }
		 if (validator.isValidInput("PackageID", strPackageId, "HTTPParameterValue",64 ,false, errorList)){
			 validatedPackageID=strPackageId;
			 //OrgCodes.getOrg(paramOrgName);
		 }
		
		IACPPackageController obj =new IACPPackageController();

		return obj.uploadPackagetoEDMS(validatedPackageID, validatedOrgName);
		

	}

}