package gov.gsa.controller

import gov.gsa.dss.helper.Authenticator
import gov.gsa.dss.helper.ResponseBuilder
import com.silanis.esl.sdk.EslClient
import com.silanis.esl.sdk.builder.PackageBuilder
import com.silanis.esl.sdk.service.*
import com.silanis.esl.api.util.EmailValidator
import com.silanis.esl.sdk.*
import com.silanis.esl.sdk.builder.*
import com.silanis.esl.sdk.internal.EslServerException
import gov.gsa.dss.helper.ExceptionHandlerService
import static com.silanis.esl.sdk.builder.DocumentPackageAttributesBuilder.newDocumentPackageAttributes
import static com.silanis.esl.sdk.builder.PackageBuilder.newPackageNamed
import static com.silanis.esl.sdk.builder.SignatureBuilder.*
import static com.silanis.esl.sdk.builder.SignerBuilder.newSignerWithEmail
import org.apache.commons.lang3.StringUtils

import java.util.Map;

public class CreatePackageFromTemplateController {
	Map<String, Object> dssUniversalConnectorFromTemplate(Map<String, Object> templateData) {
		Map<String, String> messageMap = new HashMap<String, String>();
		SignerBuilder signer;
		ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
		ResponseBuilder responseBuilder = new ResponseBuilder();
		List<Signer> placeholders;

		String successMessage;

		/*Validate the Map signatureInsertionData that comes in.*/
		messageMap = validateData(templateData);

		if (!(messageMap==null)) {
			return messageMap
		}

		String tempIdString = templateData.getAt("templateId");
		PackageId tempPackageId = new PackageId(tempIdString);

		Authenticator auth = new Authenticator();
		EslClient dssEslClient = auth.getAuth();
		try{
			DocumentPackage tempPack = dssEslClient.getPackageService().getPackage(tempPackageId);
			placeholders = tempPack.getPlaceholders();
		}
		catch (Exception exp){
			messageMap = exceptionHandlerService.parseException(exp);
			return messageMap;
		}


		/*Create the Package.*/
		PackageBuilder packageFromTemplate = newPackageNamed(templateData.packageName);
		packageFromTemplate.withSenderInfo(SenderInfoBuilder.newSenderInfo(templateData.senderEmail));

		def signersArray = templateData.signers;

		for (int j = 0; j < signersArray.size(); j++) {

			System.out.println(placeholders[j].getId());

			if ((signersArray.size()) != (placeholders.size())) {
				messageMap = exceptionHandlerService.parseValidationErrors("564");
				return messageMap;
			}

			signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
					.withFirstName(signersArray[j].getAt("signerFirstName"))
					.withLastName(signersArray[j].getAt("signerLastName"))
					.replacing(new Placeholder(placeholders[j].getId()));

			if ((templateData.containsKey(signersArray[j].getAt("noteToSigner")))||(StringUtils.isNotEmpty(signersArray[j].getAt("noteToSigner")))) {
				signer.withEmailMessage(signersArray[j].getAt("noteToSigner"));
			}
			packageFromTemplate.withSigner(signer);
		}

		try {
			/*The package attribute is set to the organization name using withAttributes() method*/
			DocumentPackage completePackage = packageFromTemplate
					.withSettings(DocumentPackageSettingsBuilder.newDocumentPackageSettings().withInPerson())
					.withAttributes(newDocumentPackageAttributes()
					.withAttribute("orgName", templateData.orgName)
					.build())
					.build();

			PackageId packageId = dssEslClient.getTemplateService().createPackageFromTemplate(tempPackageId, completePackage);
			successMessage = "Package created and available in the draft folder."

			messageMap = responseBuilder.buildSuccessResponse(successMessage, packageId.toString(), completePackage.getName())
		}
		catch (Exception e) {
			messageMap = exceptionHandlerService.parseException(e);
		}
		return messageMap;
	}
	def validateData(Map<String, Object> templateData) {
		def messageList = [];
		EmailValidator validator = new EmailValidator();
		String fileName;
		int nameLength;
		String extension;
		ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
		def messageMap;

		if ((templateData.containsKey(templateData.orgName))||(StringUtils.isEmpty(templateData.orgName))) {
			messageMap = exceptionHandlerService.parseValidationErrors("550");
		}
		if ((templateData.containsKey(templateData.packageName))||(StringUtils.isEmpty(templateData.packageName))) {
			messageMap = exceptionHandlerService.parseValidationErrors("540");
		}

		if ((templateData.containsKey(templateData.senderEmail))||(StringUtils.isEmpty(templateData.senderEmail))) {
			messageMap = exceptionHandlerService.parseValidationErrors("541");
		} else {
			if (validator.valid(templateData.senderEmail) == false) {
				messageMap = exceptionHandlerService.parseValidationErrors("542");
			}
		}

		if ((templateData.containsKey(templateData.templateId))||(StringUtils.isEmpty(templateData.templateId))) {
			messageMap = exceptionHandlerService.parseValidationErrors("563");
		}


		if ((templateData.containsKey(templateData.packageOption))||(StringUtils.isEmpty(templateData.packageOption))) {
			messageMap = exceptionHandlerService.parseValidationErrors("543");
		}
		def signersArray = templateData.signers;

		for (int j = 0; j < signersArray.size(); j++) {

			if ((templateData.containsKey(signersArray[j].getAt("signerEmail")))||(StringUtils.isEmpty(signersArray[j].getAt("signerEmail")))) {
				messageMap = exceptionHandlerService.parseValidationErrors("546");
			} else {
				if (validator.valid(signersArray[j].getAt("signerEmail")) == false) {
					messageMap = exceptionHandlerService.parseValidationErrors("547");
				}
			}


			if ((templateData.containsKey(signersArray[j].getAt("signerFirstName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerFirstName")))) {
				messageMap = exceptionHandlerService.parseValidationErrors("548");
			}

			if ((templateData.containsKey(signersArray[j].getAt("signerLastName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerLastName")))) {
				messageMap = exceptionHandlerService.parseValidationErrors("549");
			}
		}

		return messageMap;
	}
}
