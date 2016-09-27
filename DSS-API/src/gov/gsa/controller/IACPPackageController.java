package gov.gsa.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.json.JSONException;
import org.json.JSONObject;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.YamlConfig;
import gov.gsa.dss.helper.Zipper;
import gov.gsa.dss.helper.staic.ErrorMessages;

public class IACPPackageController {
	protected static String fileName="";
	protected static String strbaseURL="";
	protected static byte [] base64File;
	protected static String strOrgName;
	protected static String strPackageID;
	protected static int status;
	//@Context
    //UriInfo uriInfo;
	public Response uploadPackagetoEDMS(String PackageId, String OrgName)    
	{
		
		//strbaseURL =baseURL;
		strOrgName=OrgName;
		strPackageID=PackageId;
		try{
			YamlConfig obj = new YamlConfig();
			Map<String, String> sessionParameters = new HashMap<String, String>();

			sessionParameters.put(SessionParameter.USER, obj.getProp("edmsuser"));
			sessionParameters.put(SessionParameter.PASSWORD, obj.getProp("edmspwd"));
			sessionParameters.put(SessionParameter.ATOMPUB_URL,obj.getProp("edmsURL") );
			sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
			Session lSession = sessionFactory.getRepositories(sessionParameters).get(0).createSession();

			Map<String, Object> lProperties = new HashMap<String, Object>();
			ZippedPackage();
			Folder fol = (Folder) lSession.getObjectByPath(obj.getProp("edmspath"));
			String name = fileName;
			System.out.println(fileName);
			lProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			lProperties.put(PropertyIds.NAME, name);
			byte[] content = base64File;
			InputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(name, new BigInteger(content), "text/plain", stream);
			Document newContent1 =  fol.createDocument(lProperties, contentStream, null);
			//System.out.println("Document created: " + newContent1.getId());
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("{\"AlfrescoDocumentID\":"+newContent1.getId()+"}").build();
		}
 
		catch (Exception e)
		{
			//e.printStackTrace();
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			
			//return Response.ok(ehs.parseException(e)+"", MediaType.TEXT_PLAIN).build();
			@SuppressWarnings("unchecked")
			Map <String, String> msg = (Map<String, String>) ehs.parseException(e);

			
			//System.out.println(msg);
				
					
					@SuppressWarnings("unchecked")
					Map<String, String> parseValidationErrors =(Map<String, String>) ehs.parseException(e);
					int code =  Integer.parseInt((String) msg.get("code"));
					JSONObject json = new JSONObject(parseValidationErrors);
					return Response.status(code).type(MediaType.APPLICATION_JSON)
						.entity(json+"").build();
		}
	}
	

	protected static void ZippedPackage() throws Exception
	{
		Authenticator auth = new Authenticator();
		EslClient Client = auth.getAuth();
		PackageId packageId = new PackageId(strPackageID);
		//System.out.println(1);
		DocumentPackage DocPackage = Client.getPackage(packageId);
		Zipper zipDocs = new Zipper();
		
		//String target =strbaseURL+"retrieve/downloadDocuments?packageId="+strPackageID+"&orgName="+strOrgName;
		//System.out.println(target);
		//Client client = ClientBuilder.newClient();
		//WebTarget ret =client.target(target); //...;
		//Response response = ret.request(MediaType.APPLICATION_JSON).get();
		//status=response.getStatus();
		
		//String output = response.readEntity(String.class);
		//System.out.println(output);
		//JSONObject obj = new JSONObject(output);
		//String base64ZIP = obj.getJSONObject("Package").getString("Content");
		fileName =DocPackage.getName()+"_"+strPackageID+".zip";
		//String base64ZIPevidence = obj.getJSONObject("Package").getString("Evidence");

		base64File = zipDocs.getZip(DocPackage, Client);
		

		//return decoded;
	}

}