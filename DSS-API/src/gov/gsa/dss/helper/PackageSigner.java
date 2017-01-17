package gov.gsa.dss.helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.silanis.esl.sdk.Audit;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;

public class PackageSigner {
	
	//protected List  Signers;
	//protected List Decliners;
	final static Logger log =Logger.getLogger(PackageSigner.class);
	/**
	 * 
	 * @param strPackageId
	 * @return ArrayList of signers {Name email an Timestamp of signature}
	 * @throws FileNotFoundException
	 * @throws NamingException
	 */
	public List<Map<String, String>> getSigners(String strPackageId) throws FileNotFoundException, NamingException
	{
		PackageId packageId = new PackageId(strPackageId);
		Authenticator auth = new Authenticator();
		EslClient client = auth.getAuth();
		List <Map<String, String>> Signers = new ArrayList <Map<String, String>>();
		for (Audit aud: client.getAuditService().getAudit(packageId))
		{
			Map <String, String> SignerDetails = new HashMap<String, String>();
			if (aud.getType().equals("Click To Sign"))
				{
				SignerDetails.put("email",aud.getEmail());
				SignerDetails.put("name",aud.getUser());
				SignerDetails.put("date",aud.getDateTime());
				Signers.add(SignerDetails);	
				}
		}
		return Signers;
	}
	
	/**
	 * 
	 * @return Hashmap of decliner detail { email, name, decline date, decline comments}
	 * @throws NamingException 
	 * @throws FileNotFoundException 
	 */
	public Map<String, String> getDecliners(String strPackageId) throws FileNotFoundException, NamingException
	{
		PackageId packageId = new PackageId(strPackageId);
		Authenticator auth = new Authenticator();
		EslClient client = auth.getAuth();
		
		Map <String, String> DeclinerDetails = new HashMap<String, String>();
		for (Audit aud: client.getAuditService().getAudit(packageId))
		{
			if (aud.getType().equals("Decline"))
				{
				DeclinerDetails.put("email",aud.getEmail());
				DeclinerDetails.put("name",aud.getUser());
				DeclinerDetails.put("date",aud.getDateTime());
				DeclinerDetails.put("declinedata",aud.getData());	
				}
		}
		return DeclinerDetails;
	}

}