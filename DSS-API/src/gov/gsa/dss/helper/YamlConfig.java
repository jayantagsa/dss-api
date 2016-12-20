package gov.gsa.dss.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class YamlConfig {
	final static Logger log =Logger.getLogger(YamlConfig.class);
	private static String key;
	private static String url;

	private static Map<String, String> strMap;

	private static String jmsBrokerUrl;
	private static String dssSupportEmail;

	public YamlConfig() throws NamingException, FileNotFoundException {

		Yaml yaml = new Yaml();
		InitialContext initialContext = new InitialContext();
		Context environmentContext = (Context) initialContext.lookup("java:/comp/env");
		String connectionURL = (String) environmentContext.lookup("config");

		InputStream ios = new FileInputStream(new File(connectionURL));

		@SuppressWarnings("unchecked")
		Map<String, String> yamlAsString = (Map<String, String>) yaml.load(ios);
		strMap = yamlAsString;

		key = yamlAsString.get("apikey");
		url = yamlAsString.get("apiurl");

		jmsBrokerUrl = yamlAsString.get("jmsBrokerUrl");
		dssSupportEmail = yamlAsString.get("dssSupportEmail");
		
		try {
			ios.close();
		} catch (IOException e) {
			log.error(e);
			//e.printStackTrace();
		}

	}

	public String getProp(String prop) {
		return strMap.get(prop);
	}

	public String getKey() {
		// TODO Auto-generated method stub

		return key;
	}

	public String getURL() {
		// TODO Auto-generated method stub
		{
			return url;
		}
	}

	public String getJmsBrokerUrl() {
		// TODO Auto-generated method stub

		return jmsBrokerUrl;
	}

	public String getDssSupportEmail() {
		// TODO Auto-generated method stub

		return dssSupportEmail;
	}
}
