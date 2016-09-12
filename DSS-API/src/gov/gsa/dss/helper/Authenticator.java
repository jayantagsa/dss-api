package gov.gsa.dss.helper;

import java.io.FileNotFoundException;

import javax.naming.NamingException;

import com.silanis.esl.sdk.EslClient;

public class Authenticator {
public EslClient getAuth() throws NamingException, FileNotFoundException
{
	YamlConfig obj = new YamlConfig();
	EslClient eslClient = new EslClient( obj.getKey(), obj.getURL() );	
	//EslClient eslClient = new EslClient( "ZXZqQmxNRFFzaFVROjM3RHlTTlI2SmdJRw==", "https://sandbox.e-signlive.com/api" );
	return eslClient;

}
}
