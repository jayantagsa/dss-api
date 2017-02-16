package gov.gsa.dss.helper

import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException
import org.apache.log4j.Logger;

import com.silanis.esl.sdk.internal.EslServerException

import gov.gsa.dss.helper.staic.ErrorMessages;

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
	final static Logger log =Logger.getLogger(ExceptionHandlerService.class);
	String message;
	int code;
	String type;
	def response;

	/*This is to parse a generic Java exception*/
	def parseException (Exception exc) {

		if (exc instanceof EslServerException) {
			response = parseEslServerException(exc);
		}
		else if(exc instanceof CmisContentAlreadyExistsException)
		{
			log.error(exc);	
			log.info(exc.getMessage());
			//+"...............";
			//exc.printStackTrace();
			message = exc.getMessage();;
			type = ErrorMessages.getType("552");
			code =552;
			ResponseBuilder responseBuilder = new ResponseBuilder();
			response = responseBuilder.buildExceptionResponse(message,
					code,
					type);
		}
		else
		{
//			exc.printStackTrace();
			log.error(exc);
			message = exc.getMessage();
			type = ErrorMessages.getType("400");
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
		log.error(eslExc);
//		eslExc.printStackTrace();
		message = eslExc.getServerError().getMessage();
		code = eslExc.getServerError().getCode();
		type = eslExc.getServerError().getName();
		ResponseBuilder responseBuilder = new ResponseBuilder();
		response = responseBuilder.buildExceptionResponse(message,
				code,
				type);
	}

	/*This is to create a response for validation errors*/
	def parseValidationErrors (String code) {
		ResponseBuilder responseBuilder = new ResponseBuilder();
		String message = ErrorMessages.getMessage(code);
		String type = ErrorMessages.getType(code);
		response = responseBuilder.buildExceptionResponse(message,
				code.toInteger(),
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

