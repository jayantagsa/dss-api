package gov.gsa.dss.helper;

import java.util.HashMap;
import java.util.Map

import org.apache.log4j.Logger;

/**
 * Created by sudhangi on 7/21/2016.
 * DSSCR-503: Class to convert error, success, exception into a MAP format.
 *
 * This abstraction  will accept an exception
 * 1.) error/success code
 * 2.) descriptive message
 * 3.) Type or error
 */
public class ResponseBuilder {
	final static Logger log =Logger.getLogger(ResponseBuilder.class);
    Map<String, String> response = new HashMap<String, String>();

    Map buildExceptionResponse (String message, int code, String type) {

        response.put("message", message);
        response.put("code", Integer.toString(code));
        response.put("type", type);
        return response;
    }

    Map buildSuccessResponse (String message, String packageId, String packageName) {
        Map<String, String> packageDetails = new HashMap<String, String>();

        response.put("code", "200");
        response.put("message", message);
        packageDetails.put("packageId", packageId);
        packageDetails.put("packageName", packageName);
        response.put("packageInfo", packageDetails);
        return response;
    }
    /*This success response is for the response that we get from publishing to Active MQ*/
    Map buildSuccessResponse (String message) {
        Map<String, String> packageDetails = new HashMap<String, String>();

        response.put("code", "200");
        response.put("message", message);
        return response;
    }

}
