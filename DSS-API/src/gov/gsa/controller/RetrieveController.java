package gov.gsa.controller;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;
import com.silanis.esl.sdk.PackageStatus;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.Zipper;

public class RetrieveController {

	public Response getZippedDocuments(String strPackageId) 
	{
		try{
			
			Authenticator auth = new Authenticator();
			EslClient Client = auth.getAuth();

			PackageId packageId = new PackageId(strPackageId);


			DocumentPackage DocPackage = Client.getPackage(packageId );
			
			System.out.println(DocPackage.getStatus());
			
			
			System.out.println(DocPackage.getStatus());;
			//List <Document> Documents= DocPackage.getDocuments();
			Zipper zipDocs = new Zipper();
			String base64ZIP = Base64.encodeBase64String(zipDocs.getZip(DocPackage, Client));
				
				
			String strJSON="{\"Package\":{\"Name\":\""+DocPackage.getName()+"\", \"Content\": \""+base64ZIP+"\"}}";;
			return Response.ok(strJSON, MediaType.APPLICATION_JSON).build();

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
