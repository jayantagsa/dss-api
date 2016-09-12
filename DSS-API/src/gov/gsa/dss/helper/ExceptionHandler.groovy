package gov.gsa.dss.helper

import com.silanis.esl.sdk.internal.EslServerException

/**
 * Created by Sudhangi on 7/21/2016.
 *  DSSCR-503: Class to categorize whether the exception
 *  ESL Exception or Generic Java Exception
 *
 * This abstraction  will accept an exception and
 * 1.) print the stacktrace
 * 2.) extract the exception code, message and type
 * 3.) Send it to ResponseBuilder to create a readable Map
 */
public class ExceptionHandlerService {
    String message;
    int code;
    String type;
    def response;

    /*This is to parse a generic Java exception*/
    def parseException (Exception exc) {

        if (exc instanceof EslServerException) {
            response = parseEslServerException(exc);
        }
        else {
            exc.printStackTrace();
            message = exc.getMessage();
            type = Arrays.toString(exc.getStackTrace());
            code =400;
            ResponseBuilder responseBuilder = new ResponseBuilder();
            response = responseBuilder.buildExceptionResponse(message,
                    code,
                    type);
        }
        return response;
    }

    /*This is to parse a ESL Server Exception*/
    def parseEslServerException (EslServerException eslExc) {
        eslExc.printStackTrace();
        message = eslExc.getServerError().getMessage();
        code = eslExc.getServerError().getCode();
        type = eslExc.getServerError().getName();
        ResponseBuilder responseBuilder = new ResponseBuilder();
        response = responseBuilder.buildExceptionResponse(message,
                                                code,
                                                type);
    }

    /*This is to create a response for validation errors*/
    def parseValidationErrors (String message, int code, String type) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        response = responseBuilder.buildExceptionResponse(message,
                code,
                type);
    }
}

