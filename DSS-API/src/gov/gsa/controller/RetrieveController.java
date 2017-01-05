package gov.gsa.controller;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.Mail;
import gov.gsa.dss.helper.PackageOrgName;
import gov.gsa.dss.helper.Zipper;
import gov.gsa.dss.helper.staic.ErrorMessages;
import gov.gsa.dss.helper.staic.OrgCodes;
import gov.gsa.dss.model.RetrieveModel;
import gov.gsa.dss.views.service.Retrieve;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.Validator;
public class RetrieveController {
	final static Logger log =Logger.getLogger(RetrieveController.class);
	public Response getZippedDocuments(String paramPackageId, String paramOrgName) 
	{
		Validator validator = ESAPI.validator();
		ValidationErrorList errorList = new ValidationErrorList();
		try{
			OrgCodes.getOrg(paramOrgName);

			if (paramPackageId!=null && paramOrgName!=null && OrgCodes.getOrg(paramOrgName)!=null)
			{
				Authenticator auth = new Authenticator();
				EslClient Client = auth.getAuth();
				PackageId packageId = new PackageId(paramPackageId);
				DocumentPackage DocPackage = Client.getPackage(packageId);
				PackageOrgName pOrg = new PackageOrgName();
				String orgName =  pOrg.getOrgName(DocPackage).toString();
				if (orgName!=null && orgName.equals(paramOrgName))
				{
					Zipper zipDocs = new Zipper();
					String base64ZIP = Base64.encodeBase64String(zipDocs.getZip(DocPackage, Client));	
					RetrieveModel obj = new RetrieveModel();	
					String strJSON=obj.getJSONString(paramPackageId, base64ZIP, DocPackage.getName());
					strJSON= validator.getValidInput("", strJSON, "HTTPHeaderValue",strJSON.length() ,false, errorList);
					return Response.ok(strJSON, MediaType.APPLICATION_JSON).build();
				}
				else{
					ExceptionHandlerService ehs = new ExceptionHandlerService();
					@SuppressWarnings("unchecked")
					Map<String, String> parseValidationErrors = (Map<String, String>) ehs.parseValidationErrors(ErrorMessages.getMessage("551"), 551,ErrorMessages.getType("551") );
					JSONObject json = new JSONObject(parseValidationErrors);
					String msg = ""+ehs.parseValidationErrors(ErrorMessages.getMessage("551"), 551,ErrorMessages.getType("551") );
					log.info(msg+"\t"+json.toString());
					return Response.status(551).type(MediaType.APPLICATION_JSON).entity(json+"").build();
				}
			}
			else{
				ExceptionHandlerService ehs = new ExceptionHandlerService();
				Map<String, String> parseValidationErrors = (Map<String, String>) ehs.parseValidationErrors(ErrorMessages.getMessage("553"), 551,ErrorMessages.getType("553") );
				JSONObject json = new JSONObject(parseValidationErrors);
				return Response.status(551).type(MediaType.APPLICATION_JSON).entity(json+"").build();
			}
		}
		catch (Exception e)
		{
			log.error(e);
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			@SuppressWarnings("unchecked")
			Map<String, String> parseValidationErrors =(Map<String, String>) ehs.parseException(e);
			JSONObject json = new JSONObject(parseValidationErrors);
			String msg = ehs.parseException(e)+"";
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type(MediaType.APPLICATION_JSON)
					.entity(json+"").build();
		}

	}

}
