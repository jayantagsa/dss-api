package gov.gsa.dss.helper.staic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DSSProperties {
	Properties prop = new Properties();
	
	public Properties getProperty(String fileName) throws IOException 
	{
		DSSProperties env = new DSSProperties();
		InputStream inputStream = env.getClass().getClassLoader().getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + "emailmessages.json" + "' not found in the classpath");
		} 
		try {
			prop.load(inputStream);
			return prop;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		finally{
		inputStream.close();
		}
		
		
	}
}
