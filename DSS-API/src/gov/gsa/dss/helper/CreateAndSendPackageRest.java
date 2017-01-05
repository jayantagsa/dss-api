package gov.gsa.dss.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CreateAndSendPackageRest {

	public static void main(String[] args) throws MalformedURLException, IOException {

		String requestURL = "https://sandbox.esignlive.com/api";
		String apiKey = "Your_API_Key";

		URLConnection connection = new URL(requestURL + "/packages/" + "packageid").openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Authorization", "Basic " + apiKey);
		connection.setRequestProperty("Accept", "application/json");
		// get and write out response code
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode);

		// get and write out response
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		try {
			in.close();
		} catch (IOException e) {
			//log.error(e);
			//e.printStackTrace();
		}

		// print result
		System.out.println(response.toString());
	}
}