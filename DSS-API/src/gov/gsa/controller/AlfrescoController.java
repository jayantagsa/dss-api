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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

import gov.gsa.dss.helper.ExceptionHandlerService;

public class AlfrescoController {
	public Response uploadPackagetoEDMS(String strPackageId, String strOrgName)   
	{
		try{
			Map<String, String> sessionParameters = new HashMap<String, String>();

			sessionParameters.put(SessionParameter.USER, "dssserviceaccnt");
			sessionParameters.put(SessionParameter.PASSWORD, "ACPedms!23");
			sessionParameters.put(SessionParameter.ATOMPUB_URL, "https://edms.acuitys.com/alfresco/api/-default-/public/cmis/versions/1.1/atom");
			sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
			Session lSession = sessionFactory.getRepositories(sessionParameters).get(0).createSession();

			Map<String, Object> lProperties = new HashMap<String, Object>();

			Folder fol = (Folder) lSession.getObjectByPath("/Sites/agency-concurrence-process/documentLibrary/Signed/");
			String name = "testdocument.zip";
			lProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			lProperties.put(PropertyIds.NAME, name);
			byte[] content = getZippedPackage();
			InputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(name, new BigInteger(content), "text/plain", stream);
			Document newContent1 =  fol.createDocument(lProperties, contentStream, null);
			System.out.println("Document created: " + newContent1.getId());
			return Response.status(551).type("text/plain")
					.entity("").build();
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


	protected static byte[] getZippedPackage() throws Exception
	{
		Client client = ClientBuilder.newClient();
		WebTarget ret =client.target("http://localhost:8080/DSS-API/dss/retrieve/downloaddocuments?PackageId=af5a31d6-9c0d-4367-bd93-3561c0d755b8&orgName=RETA"); //...;
		Response response = ret.request(MediaType.APPLICATION_JSON).get();
		String output = response.readEntity(String.class);
		JSONObject obj = new JSONObject(output);
		String base64ZIP = obj.getJSONObject("Package").getString("Content");

		//String base64ZIPevidence = obj.getJSONObject("Package").getString("Evidence");

		byte[] decoded = Base64.getDecoder().decode(base64ZIP);

		return decoded;
	}

}