package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailMessages {
	final static Logger log =Logger.getLogger(EmailMessages.class);
	private static String props;

	static {
		BufferedReader reader =null;
		InputStream inputStream = null;
		try {
			EmailMessages util = new EmailMessages();
			inputStream = util.getClass().getClassLoader().getResourceAsStream("emailmessages.json");
			if (inputStream == null) {
				throw new FileNotFoundException("property file '" + "emailmessages.json" + "' not found in the classpath");
			} 
			reader = new BufferedReader(
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
		} catch (Exception e) {
			log.error(e);
		}
		finally {
			try {
				reader.close();
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
}