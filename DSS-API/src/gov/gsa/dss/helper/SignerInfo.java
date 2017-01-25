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

public class SignerInfo {

	//protected List  Signers;
	//protected List Decliners;
	final static Logger log =Logger.getLogger(SignerInfo.class);
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
				log.info(aud.getType());
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
	//Decline option is not required. 
	/*public Map<String, String> getDecliners(String strPackageId) throws FileNotFoundException, NamingException
	{
		log.info("op_out/decline");
		PackageId packageId = new PackageId(strPackageId);
		Authenticator auth = new Authenticator();
		EslClient client = auth.getAuth();

		Map <String, String> DeclinerDetails = new HashMap<String, String>();
		for (Audit aud: client.getAuditService().getAudit(packageId))
		{
			if (aud.getType().equals("Decline"))
			{
				log.info(aud.getType());
				DeclinerDetails.put("email",aud.getEmail());
				DeclinerDetails.put("name",aud.getUser());
				DeclinerDetails.put("date",aud.getDateTime());
				DeclinerDetails.put("declinedata",aud.getData());	
			}
		}
		return DeclinerDetails;
	}*/
	/**
	 * 
	 * @param strPackageId
	 * @return Hashmap of opt out detail 
	 * @throws FileNotFoundException
	 * @throws NamingException
	 */
	public Map<String, String> getOptOutDetails(String strPackageId) throws FileNotFoundException, NamingException
	{
		log.info("op_out/decline");
		PackageId packageId = new PackageId(strPackageId);
		Authenticator auth = new Authenticator();
		EslClient client = auth.getAuth();

		Map <String, String> OptOutDetails = new HashMap<String, String>();
		for (Audit aud: client.getAuditService().getAudit(packageId))
		{
			if (aud.getType().equals("Opt Out"))
			{
				log.info(aud.getType());
				OptOutDetails.put("email",aud.getEmail());
				OptOutDetails.put("name",aud.getUser());
				OptOutDetails.put("date",aud.getDateTime());
				OptOutDetails.put("declinedata",aud.getData());	
			}
		}
		return OptOutDetails;
	}
}
