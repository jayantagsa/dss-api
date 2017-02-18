package gov.gsa.dss.controller

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.YamlConfig;
import gov.gsa.dss.helper.Zipper;
import java.text.Normalizer
import java.text.Normalizer.Form

class EDMSController {
	final static Logger log =Logger.getLogger(EDMSController.class);

	protected static String fileName="";
	protected static String strbaseURL="";
	protected static byte [] base64File;
	protected static int status;
	protected static InputStream stream;

	//@Context
	//UriInfo uriInfo;
	public Response uploadPackagetoEDMS(String PackageId, String OrgName)

	{

		try{
			/*Normalize the PackageId and OrgName before sending it over for further processing*/
			String strOrgName = Normalizer.normalize(OrgName, Normalizer.Form.NFKC);
			String strPackageID = Normalizer.normalize(PackageId, Normalizer.Form.NFKC);
			packageUpload(strPackageID, strOrgName);
			
			/*return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("{\"AlfrescoDocumentID\":"+newContent1.getId()+"}").build();*/
					return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("AlfrescoDocumentID").build();
		}

		catch (Exception e)
		{
			log.error(e);
			ExceptionHandlerService ehs = new ExceptionHandlerService();

			@SuppressWarnings("unchecked")
					Map <String, String> msg = (Map<String, String>) ehs.parseException(e);
			@SuppressWarnings("unchecked")
					Map<String, String> parseValidationErrors =(Map<String, String>) ehs.parseException(e);
			int code =  Integer.parseInt((String) msg.get("code"));
			JSONObject json = new JSONObject(parseValidationErrors);
			return Response.status(code).type(MediaType.APPLICATION_JSON)
					.entity(json).build();
		}
		finally {
			stream.close();
		}
	}

	public void packageUpload(String PackageId, String OrgName) throws Exception
	{
		
		YamlConfig obj = new YamlConfig();
		Map<String, String> sessionParameters = new HashMap<String, String>();

		sessionParameters.put(SessionParameter.USER, obj.getProp("edmsuser"));
		sessionParameters.put(SessionParameter.PASSWORD, obj.getProp("edmspwd"));
		sessionParameters.put(SessionParameter.ATOMPUB_URL,obj.getProp("edmsURL") );
		sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Session lSession = sessionFactory.getRepositories(sessionParameters).get(0).createSession();

		Map<String, Object> lProperties = new HashMap<String, Object>();
		ZippedPackage(PackageId, OrgName);
		def appendPathUrl;
			appendPathUrl = "edmspath"+OrgName;
		
		log.info(appendPathUrl);
		Folder fol = (Folder) lSession.getObjectByPath(obj.getProp(appendPathUrl));

		String name = fileName;

		log.info(fileName);
		lProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		lProperties.put(PropertyIds.NAME, name);

		byte[] content = base64File;
		stream = new ByteArrayInputStream(content);
		ContentStream contentStream = new ContentStreamImpl(name, new BigInteger(content), "text/plain", stream);
		Document newContent1 =  fol.createDocument(lProperties, contentStream, null);
		
		
	}

	protected static void ZippedPackage(String strPackageID, String OrgName) throws Exception
	{
		Authenticator auth = new Authenticator();
		EslClient Client = auth.getAuth();
		PackageId packageId = new PackageId(strPackageID);
		DocumentPackage DocPackage = Client.getPackage(packageId);
		Zipper zipDocs = new Zipper();
		fileName =DocPackage.getName()+"_"+strPackageID+".zip";
		base64File = zipDocs.getZip(DocPackage, Client);
	}
}
