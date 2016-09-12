package gov.gsa.controller;

import org.apache.commons.codec.binary.Base64;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;
import com.silanis.esl.sdk.PackageStatus;

import gov.gsa.dss.helper.Authenticator;
import gov.gsa.dss.helper.Zipper;

public class RetrieveController {

	public String getZippedDocuments(String strPackageId) throws Exception
	{
		
			
			Authenticator auth = new Authenticator();
			EslClient Client = auth.getAuth();

			PackageId packageId = new PackageId(strPackageId);


			DocumentPackage DocPackage = Client.getPackage(packageId );
			
			System.out.println(DocPackage.getStatus());
			
			if(DocPackage.getStatus().equals(PackageStatus.COMPLETED))
			{
			System.out.println(DocPackage.getStatus());;
			//List <Document> Documents= DocPackage.getDocuments();
			Zipper zipDocs = new Zipper();
			String base64ZIP = Base64.encodeBase64String(zipDocs.getZip(DocPackage, Client));
				
				
			String strJSON="{\"Package\":{\"Name\":\""+DocPackage.getName()+"\", \"Content\": \""+base64ZIP+"\"}}";;
				
			return (strJSON);
			}
			else{
				throw new Exception ("Validation Error: Package you are trying to download is incomplete");
			}
	}
	
}
