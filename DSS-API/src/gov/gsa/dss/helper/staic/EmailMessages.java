package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class EmailMessages {

	private static String props;

	static {
		try {
			EmailMessages util = new EmailMessages();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(util.getPropertiesFromClasspath("emailmessages.json")));
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
	private EmailMessages() {
	}

	public static String getSubject(String key) throws JSONException {
		JSONObject obj = new JSONObject(props);
		JSONObject subject = obj.getJSONObject("subject");
		return subject.getString(key);
	}

	public static String getMessage(String key) throws JSONException {
		JSONObject obj = new JSONObject(props);
		JSONObject subject = obj.getJSONObject("message");
		return subject.getString(key);
	}

	/**
	 * loads properties file from classpath
	 *
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	InputStream getPropertiesFromClasspath(String propFileName) throws IOException {
		InputStream inputStream = null;

		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream == null) {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			} else {
				return inputStream;
			}
		}

		finally {
			inputStream.close();
		}

	}
}