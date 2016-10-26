package gov.gsa.controller

import gov.gsa.dss.helper.Authenticator
import gov.gsa.dss.helper.DSSQueueManagement;
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
import org.apache.commons.lang3.RandomStringUtils
import sun.misc.BASE64Decoder

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class CreatePackageFromDocLayoutController {
	Map<String, Object> dssUniversalConnectorFromDocLayout(Map<String, Object> docLayoutData) {
		Map<String, String> messageMap = new HashMap<String, String>();
		SignerBuilder signer;
		ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
		ResponseBuilder responseBuilder = new ResponseBuilder();
		List<Signer> placeholders;
		String docName = null;
		String docId = null;
		String filePath = null;
		String fileName = null;
		String docLayoutName = null;
		int itr = 0
		List <String> listLayoutIds = new ArrayList<String>();
		CreatePackageController createPackageContoller = new CreatePackageController();
		String successMessage;

		/*Validate the Map templateData that comes in.*/
		messageMap = validateData(docLayoutData);
		if (!(messageMap==null)) {
			return messageMap
		}

		Authenticator auth = new Authenticator();
		EslClient dssEslClient = auth.getAuth();


		/*Create the Package.*/
		PackageBuilder package1 = newPackageNamed(docLayoutData.packageName);
		package1.withSenderInfo(SenderInfoBuilder.newSenderInfo(docLayoutData.senderEmail));

		def documentsMap = docLayoutData.documents;
		def numOfDocs = documentsMap.size();
		for (int i = 0; i < documentsMap.size(); i++) {
			docName = (documentsMap[i].getAt("document").getAt("documentName"));
			docLayoutName = (documentsMap[i].getAt("document").getAt("layoutName"));

			/*Iterate through all the layouts*/
			List<DocumentPackage> layouts = dssEslClient.getLayoutService().getLayouts(Direction.DESCENDING, new PageRequest(itr, 20));
			for (int m = 0; m < layouts.size(); m++) {

				DocumentPackage myLayout = layouts[m];
				if (myLayout.getName() == docLayoutName) {
					def docLayoutId = myLayout.getId();
					listLayoutIds.add(docLayoutId.toString());
//					placeholders = myLayout.getPlaceholders();
				}
			}
		
			/*Convert base64 encoded file String into InputStream*/
			InputStream bufferedInputStream = null;
			try {
				bufferedInputStream = createPackageContoller.decodeBase64String(documentsMap[i].getAt("document").getAt("documentContent"));
			}
			catch (Exception e) {
				/*This is when the base64 encoded file is corrupt and ends up with an exception while decoding it*/
				messageMap = exceptionHandlerService.parseValidationErrors(567);
				return messageMap
			}
			File tmpPDFFile = createPackageContoller.writePDFFileToLocalDisk(bufferedInputStream)
			filePath = tmpPDFFile.getCanonicalPath()
			bufferedInputStream.close()

			Document mydoc = DocumentBuilder.newDocumentWithName(docName)
					.fromFile(filePath)
					.build();

			def signersArray = documentsMap[i].getAt("document").getAt("signers");
			for (int j = 0; j < signersArray.size(); j++) {

				if (docLayoutData.enableSigningOrder == true) {
					signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
							.withFirstName(signersArray[j].getAt("signerFirstName"))
							.withLastName(signersArray[j].getAt("signerLastName"))
							.signingOrder(j);
				} else {
					signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
							.withFirstName(signersArray[j].getAt("signerFirstName"))
							.withLastName(signersArray[j].getAt("signerLastName"));
				}

				if ((docLayoutData.containsKey(signersArray[j].getAt("noteToSigner")))||(StringUtils.isNotEmpty(signersArray[j].getAt("noteToSigner")))) {
					signer.withEmailMessage(signersArray[j].getAt("noteToSigner"));
				}
				package1.withSigner(signer);
			}
			package1.withDocument(mydoc);
			tmpPDFFile.delete();
		}
		try {
			/*The package attribute is set to the organization name using withAttributes() method*/
			DocumentPackage completePackage = package1
					.withAttributes(newDocumentPackageAttributes()
					.withAttribute("orgName", docLayoutData.orgName)
					.build())
					.build();
					
			/*Make sure the number of layouts and number of documents is the same. Because if the user gives incorrect layout name this count will differ.*/		
			def numOfLayouts = listLayoutIds.size();
			if (numOfLayouts != numOfDocs) {
				messageMap = exceptionHandlerService.parseValidationErrors("566");
				return messageMap;
			}

			/*Add description to the package*/
			if ((docLayoutData.containsKey(docLayoutData.packageDescription))||(StringUtils.isNotEmpty(docLayoutData.packageDescription))) {
				completePackage.setDescription(docLayoutData.packageDescription);
			}
			PackageId packageId = dssEslClient.createPackage(completePackage);

			List<com.silanis.esl.sdk.Document> packageDocuments= dssEslClient.getPackage(packageId).getDocuments();		

			for (int k = 1; k < packageDocuments.size(); k++) {
				docId = packageDocuments[k].getId().toString();
				dssEslClient.getLayoutService().applyLayout(packageId, docId, listLayoutIds[k-1]);
			}
			
			if (docLayoutData.packageOption == "createSend") {
				dssEslClient.sendPackage(packageId)
				successMessage = "Package created and sent."
			} else {
				successMessage = "Package created and available in the draft folder."
			}
			messageMap = responseBuilder.buildSuccessResponse(successMessage, packageId.toString(), completePackage.getName())
		}
		catch (Exception e) {
			messageMap = exceptionHandlerService.parseException(e);
		}
		return messageMap
	}

	def validateData(Map<String, Object> data) {
		def messageList = [];
		EmailValidator validator = new EmailValidator();
		String fileName;
		int nameLength;
		String extension;
		ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
		def messageMap;

		if ((data.containsKey(data.orgName))||(StringUtils.isEmpty(data.orgName))) {
			messageMap = exceptionHandlerService.parseValidationErrors("550");
		}
		if ((data.containsKey(data.packageName))||(StringUtils.isEmpty(data.packageName))) {
			messageMap = exceptionHandlerService.parseValidationErrors("540");
		}

		if ((data.containsKey(data.senderEmail))||(StringUtils.isEmpty(data.senderEmail))) {
			messageMap = exceptionHandlerService.parseValidationErrors("541");
		} else {
			if (validator.valid(data.senderEmail) == false) {
				messageMap = exceptionHandlerService.parseValidationErrors("542");
			}
		}


		/*This is a Boolean type variable so will need to check for null value and not 'if String empty'.
		 If the user does not provide a value for 'enableSigningOder' then it will be assigned a default value of
		 'false'*/
		if ((data.containsKey(data.enableSigningOrder))||(data.enableSigningOrder == null)) {
			data.enableSigningOrder = false;
		}

		if ((data.containsKey(data.packageOption))||(StringUtils.isEmpty(data.packageOption))) {
			messageMap = exceptionHandlerService.parseValidationErrors("543");
		}

		def documentsMap = data.documents;
		for (int i = 0; i < documentsMap.size(); i++) {
			if ((data.containsKey(documentsMap[i].getAt("document").getAt("documentName")))||(StringUtils.isEmpty(documentsMap[i].getAt("document").getAt("documentName")))) {
				messageMap = exceptionHandlerService.parseValidationErrors("544");
			} else {
				/*Make sure that the user gives a filename with PDF extension only*/
				fileName = documentsMap[i].getAt("document").getAt("documentName");
				nameLength = fileName.length();
				extension = fileName.substring((nameLength - 3), nameLength);
				if ((!(extension.equalsIgnoreCase("pdf"))) || (nameLength == 3)) {
					messageMap = exceptionHandlerService.parseValidationErrors("562");
				}
			}
			if ((data.containsKey(documentsMap[i].getAt("document").getAt("layoutName")))||(StringUtils.isEmpty(documentsMap[i].getAt("document").getAt("layoutName")))) {
				messageMap = exceptionHandlerService.parseValidationErrors("565");
			}

			if ((data.containsKey(documentsMap[i].getAt("document").getAt("documentContent")))||(StringUtils.isEmpty(documentsMap[i].getAt("document").getAt("documentContent")))) {
				/*TODO Verify that the file is of type PDF*/
				messageMap = exceptionHandlerService.parseValidationErrors("545");
			}
			def signersArray = documentsMap[i].getAt("document").getAt("signers");
			for (int j = 0; j < signersArray.size(); j++) {

				if ((data.containsKey(signersArray[j].getAt("signerEmail")))||(StringUtils.isEmpty(signersArray[j].getAt("signerEmail")))) {
					messageMap = exceptionHandlerService.parseValidationErrors("546");
				} else {
					if (validator.valid(signersArray[j].getAt("signerEmail")) == false) {
						messageMap = exceptionHandlerService.parseValidationErrors("547");
					}
				}


				if ((data.containsKey(signersArray[j].getAt("signerFirstName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerFirstName")))) {
					messageMap = exceptionHandlerService.parseValidationErrors("548");
				}

				if ((data.containsKey(signersArray[j].getAt("signerLastName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerLastName")))) {
					messageMap = exceptionHandlerService.parseValidationErrors("549");
				}
			}
		}
		return messageMap;
	}
}
