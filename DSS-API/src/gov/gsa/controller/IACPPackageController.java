package gov.gsa.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
<<<<<<< HEAD
import org.apache.log4j.Logger;
import org.json.JSONException;
=======
>>>>>>> refs/heads/DSS-Sprint22_518_merge
import org.json.JSONObject;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.ExceptionHandlerService;
import gov.gsa.dss.helper.YamlConfig;
import gov.gsa.dss.helper.Zipper;
<<<<<<< HEAD
import gov.gsa.dss.helper.staic.ErrorMessages;
import gov.gsa.dss.views.integration.IacpPackage;
=======
>>>>>>> refs/heads/DSS-Sprint22_518_merge

public class IACPPackageController {
<<<<<<< HEAD
	final static Logger log =Logger.getLogger(IACPPackageController.class);
	protected static String fileName="";
	protected static String strbaseURL="";
	protected static byte [] base64File;
=======
	protected static String fileName = "";
	protected static String strbaseURL = "";
	protected static byte[] base64File;
>>>>>>> refs/heads/DSS-Sprint22_518_merge
	protected static String strOrgName;
	protected static String strPackageID;
	protected static int status;
	protected InputStream stream;
	
	// @Context
	// UriInfo uriInfo;
	public Response uploadPackagetoEDMS(String PackageId, String OrgName) throws IOException {
		strOrgName = OrgName;
		strPackageID = PackageId;
		try {
			YamlConfig obj = new YamlConfig();
			Map<String, String> sessionParameters = new HashMap<String, String>();

			sessionParameters.put(SessionParameter.USER, obj.getProp("edmsuser"));
			sessionParameters.put(SessionParameter.PASSWORD, obj.getProp("edmspwd"));
			sessionParameters.put(SessionParameter.ATOMPUB_URL, obj.getProp("edmsURL"));
			sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
			Session lSession = sessionFactory.getRepositories(sessionParameters).get(0).createSession();

			Map<String, Object> lProperties = new HashMap<String, Object>();
			ZippedPackage();
			Folder fol = (Folder) lSession.getObjectByPath(obj.getProp("edmspath"));
			String name = fileName;
			log.info(fileName);
			lProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			lProperties.put(PropertyIds.NAME, name);
			byte[] content = base64File;
			stream = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(name, new BigInteger(content), "text/plain", stream);
<<<<<<< HEAD
			Document newContent1 =  fol.createDocument(lProperties, contentStream, null);
			//log.info("Document created: " + newContent1.getId());
=======
			Document newContent1 = fol.createDocument(lProperties, contentStream, null);
>>>>>>> refs/heads/DSS-Sprint22_518_merge
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("{\"AlfrescoDocumentID\":" + newContent1.getId() + "}").build();
		}
<<<<<<< HEAD
 
		catch (Exception e)
		{
			log.error(e);
=======
		/* This exception will be parsed using DSS ExceptionHandlerService, so we will need to capture the broad exception here.*/ 
		catch (Exception e) {
>>>>>>> refs/heads/DSS-Sprint22_518_merge
			ExceptionHandlerService ehs = new ExceptionHandlerService();
			@SuppressWarnings("unchecked")
<<<<<<< HEAD
			Map <String, String> msg = (Map<String, String>) ehs.parseException(e);

			
			//log.info(msg);
				
					
					@SuppressWarnings("unchecked")
					Map<String, String> parseValidationErrors =(Map<String, String>) ehs.parseException(e);
					int code =  Integer.parseInt((String) msg.get("code"));
					JSONObject json = new JSONObject(parseValidationErrors);
					return Response.status(code).type(MediaType.APPLICATION_JSON)
						.entity(json+"").build();
=======
			Map<String, String> msg = (Map<String, String>) ehs.parseException(e);
			@SuppressWarnings("unchecked")
			Map<String, String> parseValidationErrors = (Map<String, String>) ehs.parseException(e);
			int code = Integer.parseInt((String) msg.get("code"));
			JSONObject json = new JSONObject(parseValidationErrors);
			return Response.status(code).type(MediaType.APPLICATION_JSON).entity(json + "").build();
		}
		finally {
			stream.close();
>>>>>>> refs/heads/DSS-Sprint22_518_merge
		}
	}

	protected static void ZippedPackage() throws Exception {
		Authenticator auth = new Authenticator();
		EslClient Client = auth.getAuth();
		PackageId packageId = new PackageId(strPackageID);
		DocumentPackage DocPackage = Client.getPackage(packageId);
		Zipper zipDocs = new Zipper();
<<<<<<< HEAD
		

		fileName =DocPackage.getName()+"_"+strPackageID+".zip";

=======
		fileName = DocPackage.getName() + "_" + strPackageID + ".zip";
>>>>>>> refs/heads/DSS-Sprint22_518_merge
		base64File = zipDocs.getZip(DocPackage, Client);
	}

}