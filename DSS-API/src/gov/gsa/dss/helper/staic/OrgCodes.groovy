package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrgCodes {
	final static Logger log =Logger.getLogger(OrgCodes.class);
	private static String props;

	static {
		try {
			OrgCodes util = new OrgCodes();
			BufferedReader reader = new BufferedReader(new InputStreamReader(util.getPropertiesFromClasspath("orgcode.json")));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			props = out.toString();
			reader.close();
		}
		catch (FileNotFoundException e) {
			log.error(e);
		}
		catch (IOException e) {
<<<<<<< HEAD
			log.error(e);
=======
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
>>>>>>> refs/heads/DSS-Sprint22_518_merge
		}
	}

	// private constructor
	private OrgCodes()
	{
	}

	public static String getOrg(String key)
	{
		try{
			JSONObject obj = new JSONObject(props);
			return obj.getString(key);
		}
		catch(JSONException e)
		{
			log.error(e);
			return null;
		}

	}

	public static List getOrgList()
	{
		List<String> orgList = new ArrayList<String>();

		try
		{
			OrgCodes util = new OrgCodes();
			BufferedReader reader = new BufferedReader(new InputStreamReader(util.getPropertiesFromClasspath("orgcode.json")));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			HashMap<String,Object> mappedData =
					new ObjectMapper().readValue(out.toString(), HashMap.class);

			for (String value : mappedData.values())
				orgList.add(value);
			//System.out.println("orglist:");
			//System.out.println(orgList);
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			log.error(e);
		}
		catch (IOException e)
		{
			log.error(e);
		}
		return orgList;
	}





	/**
	 * loads properties file from classpath
	 *
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	private InputStream getPropertiesFromClasspath(String propFileName) throws IOException
	{
		InputStream inputStream = null;
		try
		{
			inputStream =
					this.getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream == null)
			{
				throw new FileNotFoundException("property file '" + propFileName
				+ "' not found in the classpath");
			}
			else {
				return inputStream;
			}
			inputStream.close();
		}
		catch (IOException e)
		{
			log.error(e);
		}
	}
}