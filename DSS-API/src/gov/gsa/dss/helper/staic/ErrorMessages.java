package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorMessages {

	private static String props;

	static {
		try {
			ErrorMessages util = new ErrorMessages();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(util.getPropertiesFromClasspath("errorcodes.json")));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			props = out.toString();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		/*This nputStream needs to be closed but a better approach should be figured out to do it. 
		 * Closing it here causes problems in accessing the stream further ahead which results in NullPointerException */	
//			inputStream.close();
		}

	}
}