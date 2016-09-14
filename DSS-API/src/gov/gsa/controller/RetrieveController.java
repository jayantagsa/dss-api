package gov.gsa.controller;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.EslException;
import com.silanis.esl.sdk.PackageId;
import com.silanis.esl.sdk.PackageStatus;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.Zipper;
import gov.gsa.dss.model.RetrieveModel;

public class RetrieveController {

	public Response getZippedDocuments(String strPackageId, String strOrgName) 
	{
		System.out.println();
		try{
			
			Authenticator auth = new Authenticator();
			EslClient Client = auth.getAuth();

			PackageId packageId = new PackageId(strPackageId);


			DocumentPackage DocPackage = Client.getPackage(packageId);
			System.out.println("pokj");
			System.out.println(DocPackage.getAttributes().getContents());
			if (DocPackage.getAttributes().getContents()!=null && DocPackage.getAttributes().getContents().get("orgName").toString().equals(strOrgName))
			{
			System.out.println(DocPackage.getStatus());
			
			
			System.out.println(DocPackage.getStatus());;
			//List <Document> Documents= DocPackage.getDocuments();
			Zipper zipDocs = new Zipper();
			String base64ZIP = Base64.encodeBase64String(zipDocs.getZip(DocPackage, Client));
				
			RetrieveModel obj = new RetrieveModel();	
			String strJSON=obj.getJSONString(strPackageId, base64ZIP, DocPackage.getName());
			return Response.ok(strJSON, MediaType.APPLICATION_JSON).build();

			}
			else{
				ExceptionHandlerService ehs = new ExceptionHandlerService();
				String msg = ""+ehs.parseValidationErrors("Validation Error: Organization Name not provided or is empty.", 550,"Validation Error." );
				return Response.status(550).type("text/plain")
		                .entity(msg+"").build();
			}
			//return (strJSON);
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ExceptionHandlerService ehs = new ExceptionHandlerService();
				
				//return Response.ok(ehs.parseException(e)+"", MediaType.TEXT_PLAIN).build();
				String msg = ehs.parseException(e)+"";
				;
				int code = Integer.parseInt( msg.split(",")[0].split("=")[1]);
				return Response.status(code).type("text/plain")
		                .entity(ehs.parseException(e)+"").build();
				
			}
	}
	
}
