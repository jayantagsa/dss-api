package gov.gsa.controller;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.Zipper;
import gov.gsa.dss.helper.staic.ErrorMessages;
import gov.gsa.dss.helper.staic.OrgCodes;
import gov.gsa.dss.model.RetrieveModel;

public class RetrieveController {

	public Response getZippedDocuments(String strPackageId, String strOrgName) 
	{
		try{
			OrgCodes.getOrg(strOrgName);
			
			if (strPackageId!=null && strOrgName!=null && OrgCodes.getOrg(strOrgName)!=null)
			{
			Authenticator auth = new Authenticator();
			EslClient Client = auth.getAuth();

			PackageId packageId = new PackageId(strPackageId);
			System.out.println(1);
			DocumentPackage DocPackage = Client.getPackage(packageId);
			
			String orgName = DocPackage.getAttributes().getContents().get("orgName").toString();
			if (orgName.equals("") || (orgName == "null")) {
				if (DocPackage.getName().contains("ACP")) {
					orgName="IACP";
				}
				else
				{orgName=null;}
			}
			//Check for valid orgnames
			
			if (orgName!=null && orgName.equals(strOrgName))
			{
				Zipper zipDocs = new Zipper();
				String base64ZIP = Base64.encodeBase64String(zipDocs.getZip(DocPackage, Client));	
				RetrieveModel obj = new RetrieveModel();	
				String strJSON=obj.getJSONString(strPackageId, base64ZIP, DocPackage.getName());
				return Response.ok(strJSON, MediaType.APPLICATION_JSON).build();
			}
			else{
				System.out.println(23);
				ExceptionHandlerService ehs = new ExceptionHandlerService();
				Map<String, String> parseValidationErrors = (Map<String, String>) ehs.parseValidationErrors(ErrorMessages.getMessage("551"), 551,ErrorMessages.getType("551") );
				
				JSONObject json = new JSONObject(parseValidationErrors);

				String msg = ""+ehs.parseValidationErrors(ErrorMessages.getMessage("551"), 551,ErrorMessages.getType("551") );
				System.out.println(msg+"\t"+json.toString());
				
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
			e.printStackTrace();
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			@SuppressWarnings("unchecked")
			Map<String, String> parseValidationErrors =(Map<String, String>) ehs.parseException(e);
			JSONObject json = new JSONObject(parseValidationErrors);

			//return Response.ok(ehs.parseException(e)+"", MediaType.TEXT_PLAIN).build();
			String msg = ehs.parseException(e)+"";
			;
			int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
			return Response.status(code).type(MediaType.APPLICATION_JSON)
					.entity(json+"").build();

		}
	}

}
