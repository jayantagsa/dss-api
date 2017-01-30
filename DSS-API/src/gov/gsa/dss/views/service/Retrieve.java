package gov.gsa.dss.views.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import gov.gsa.dss.controller.RetrieveController;
import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.Validator;
import gov.gsa.dss.helper.staic.OrgCodes;
import gov.gsa.dss.views.integration.IacpPackage;
@Path("/package")

public class Retrieve {
	
	static Logger log =Logger.getLogger(Retrieve.class);
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

		RetrieveController obj =new RetrieveController();

		return obj.getZippedDocuments(validatedPackageID,validatedOrgName);

	}

}

