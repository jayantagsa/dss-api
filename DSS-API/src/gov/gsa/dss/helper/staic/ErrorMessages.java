package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorMessages {
	final static Logger log =Logger.getLogger(ErrorMessages.class);
	private static String props;

	static {
		InputStream inputStream = null;
		try {
			
			ErrorMessages util = new ErrorMessages();
			inputStream=util.getPropertiesFromClasspath("errorcodes.json");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			props = out.toString();
			reader.close();
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
	}

	// private constructor
	private ErrorMessages() {
	}

	public static String getMessage(String key) throws JSONException {
		JSONObject obj = new JSONObject(props);
		return obj.getJSONObject(key).getString("message");
	}

	public static String getType(String key) throws JSONException {

		JSONObject obj = new JSONObject(props);
		return obj.getJSONObject(key).getString("type");
	}

	/**
	 * loads properties file from classpath
	 *
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	private InputStream getPropertiesFromClasspath(String propFileName) throws IOException {
		InputStream inputStream = null;

		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream == null) {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			} else {
				return inputStream;
			}
		} finally {
			//inputStream.close();
		}

	}
}