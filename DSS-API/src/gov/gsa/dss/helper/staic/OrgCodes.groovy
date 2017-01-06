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
	private static InputStream inputStream = null;
	private static BufferedReader reader;
	static {


		try {
			OrgCodes util = new OrgCodes();
			inputStream =
					util.getClass().getClassLoader().getResourceAsStream("orgcode.json");

			if (inputStream == null)
			{
				throw new FileNotFoundException("property file '" + "orgcode.json"
				+ "' not found in the classpath");
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			props = out.toString();
		}
		catch (FileNotFoundException e) {
			log.error(e);
		}
		catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}
		finally{
			try {
				reader.close();
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
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
		OrgCodes util = new OrgCodes();
		log.info(props);
		HashMap<String,Object> mappedData =
				new ObjectMapper().readValue(props, HashMap.class);

		for (String value : mappedData.values())
		{
			
			orgList.add(value);
		}
		return orgList;
	}

}