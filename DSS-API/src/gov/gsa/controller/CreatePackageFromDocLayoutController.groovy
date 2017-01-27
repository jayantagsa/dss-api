package gov.gsa.controller

import gov.gsa.dss.helper.Authenticator
import gov.gsa.dss.helper.DSSQueueManagement;
import gov.gsa.dss.helper.FileOperations
import gov.gsa.dss.helper.PackageOperations
import gov.gsa.dss.helper.ResponseBuilder

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
import org.apache.log4j.Logger;
import org.apache.commons.lang3.RandomStringUtils
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Spliterators.AbstractDoubleSpliterator

/**
 * @author SSuthrave
 * @param docLayoutData Map of all the data required for the API request 
 * @return messageMap Error or Success
 * This API is used create Package using the layout option of eSignLive.
 * Following is the algorithm of the implementaion:
 * 1. Validate the json data that has been received
 * 2. Build a package, but do not create one.
 * 3. Iterate through all the attachments and add them to the package
 * 4. Iterate through the documents
 *     a. Iterate through all the layouts in eSL and get all placeholders for the layouts that match the received data 
 *     b. Iterate through signer array and add all the signers to the package.
 *     c. For repeated signers make sure to replace it with a temporary placeholder
 *     d. Add note to each individual signer
 * 5. Add package attributes
 * 6. Add package description
 * 7. Create package
 * 8. Retrieve package documents
 * 9. Iterate through the documents and apply individual layouts to each document
 * 10. Again retrieve package documents and iterate through them to replace temporary placeholder 
 * 11. Remove the temporary signer
 * 12. Update package document with all the signatures
 * 13. Iterate through all the empty placeholders and delete them
 * */

public class CreatePackageFromDocLayoutController {
	final static Logger log =Logger.getLogger(CreatePackageFromDocLayoutController.class);
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
		List <Placeholder> listPlaceHolderObj = new ArrayList<Placeholder>();
		List <Placeholder> listRequiredSignerPlaceHolder = new ArrayList<Placeholder>();
		List <Placeholder> arrayToBeAddedSigner = new ArrayList<Placeholder>();
		List <Signer> arrayToBeAddedSignerObject = new ArrayList<Signer>();
		List <SignerBuilder> listSignerBuilders = new ArrayList<SignerBuilder>();
		boolean signerExists;

		Placeholder pholder;

		FileOperations fileOps = new FileOperations();
		PackageOperations packOps = new PackageOperations();
		String successMessage;
		def numOfAttachments = 0;
		def numSigners = 0;


		/**
		 * Step 1- Validate the Map templateData that comes in.
		 * */
		messageMap = validateData(docLayoutData);
		if (!(messageMap==null)) {
			return messageMap
		}

		Authenticator auth = new Authenticator();
		EslClient dssEslClient = auth.getAuth();


		/*Step 2 - Create the Package.*/
		PackageBuilder package1 = newPackageNamed(docLayoutData.packageName);
		package1.withSenderInfo(SenderInfoBuilder.newSenderInfo(docLayoutData.senderEmail));

		/*Step 3 - Iterate through the attachment map loop--Start*/
		def attachmentsMap = docLayoutData.attachments;
		/*if the sender does not have a key for attachments*/
		if (attachmentsMap != null) {
			numOfAttachments = attachmentsMap.size();
			if (numOfAttachments !=0) {
				for (int i = 0; i < attachmentsMap.size(); i++) {
					docName = (attachmentsMap[i].getAt("attachment").getAt("attachmentName"));

					/*Convert base64 encoded file String into InputStream*/
					InputStream bufferedInputStreamForAttachments = null;
					try {
						bufferedInputStreamForAttachments = fileOps.decodeBase64String(attachmentsMap[i].getAt("attachment").getAt("attachmentContent"));
					}
					catch (Exception e) {
						/*This is when the base64 encoded file is corrupt and ends up with an exception while decoding it*/
						messageMap = exceptionHandlerService.parseValidationErrors(567);
						return messageMap
					}
					File tmpAttacheFile = fileOps.writePDFFileToLocalDisk(bufferedInputStreamForAttachments)
					filePath = tmpAttacheFile.getCanonicalPath()
					bufferedInputStreamForAttachments.close()

					Document myAttachment = DocumentBuilder.newDocumentWithName(docName)
							.fromFile(filePath)
							.build();


					package1.withDocument(myAttachment);
					tmpAttacheFile.delete();
				}
			}
		}
		/*Step 3 - Iterate through the attachment map loop--End*/

		/*Step 4 - Iterate through the documents map loop--Start*/
		def documentsMap = docLayoutData.documents;
		def numOfDocs = documentsMap.size();
		/*initialize inner loop, so that counter for signer sequence doesn't get reset*/
		int signersequence = 0;

		for (int i = 0; i < documentsMap.size(); i++) {
			docName = (documentsMap[i].getAt("document").getAt("documentName"));
			docLayoutName = (documentsMap[i].getAt("document").getAt("layoutName"));

			/*Step 4a - Iterate through all the layouts*/
			List<DocumentPackage> layouts = dssEslClient.getLayoutService().getLayouts(Direction.DESCENDING, new PageRequest(itr, 100));
			for (int m = 0; m < layouts.size(); m++) {

				DocumentPackage myLayout = layouts[m];
				if (myLayout.getName() == docLayoutName) {
					def docLayoutId = myLayout.getId();
					listLayoutIds.add(docLayoutId.toString());
					placeholders = myLayout.getPlaceholders();
					for (int p = 0; p < placeholders.size(); p++) {
						listPlaceHolderObj.add(placeholders[p]);
					}
				}
			}

			/*Convert base64 encoded file String into InputStream*/
			InputStream bufferedInputStream = null;
			try {
				bufferedInputStream = fileOps.decodeBase64String(documentsMap[i].getAt("document").getAt("documentContent"));
			}
			catch (Exception e) {
				log.error(e);
				/*This is when the base64 encoded file is corrupt and ends up with an exception while decoding it*/
				messageMap = exceptionHandlerService.parseValidationErrors("567");
				return messageMap
			}
			File tmpPDFFile = fileOps.writePDFFileToLocalDisk(bufferedInputStream)
			filePath = tmpPDFFile.getCanonicalPath()
			bufferedInputStream.close()

			Document mydoc = DocumentBuilder.newDocumentWithName(docName)
					.fromFile(filePath)
					.build();

			def signersArray = documentsMap[i].getAt("document").getAt("signers");
			for (int j = 0; j < signersArray.size(); j++) {
				for (int h = 0; h < listPlaceHolderObj.size(); h++) {
					Signer pSigner = listPlaceHolderObj[h];
					/*Check to see if the received placeholder name is same as the placeholder name from the original list*/
					if(pSigner.getPlaceholderName()==signersArray[j].getAt("placeHolderName")){
						pholder = new Placeholder(pSigner.getPlaceholderName());
					}
				}

				/*If a certain placeholder name that is received does not exists in the original placeholder list, then send a message back.
				 * This is when the user sends an incorrect placeholder name*/
				if(pholder==null) {
					messageMap = exceptionHandlerService.parseValidationErrors("569");
					return messageMap
				}

				signerExists = false;
				for (int r = 0; r < listSignerBuilders.size(); r++) {
					SignerBuilder objSignerBuilder = listSignerBuilders[r];
					String emailValue = objSignerBuilder.getAt("email");
					/*Step 4c - For repeated signers make sure to replace it with a temporary placeholder. Append the original email id with 'dsssignerph'*/
					if (signersArray[j].getAt("signerEmail") == emailValue) {
						String dummySignerEmail = "dsssignerph"+ emailValue;
						signer=SignerBuilder.newSignerWithEmail(dummySignerEmail)
								.withFirstName(signersArray[j].getAt("signerFirstName"))
								.withLastName(signersArray[j].getAt("signerLastName")).replacing(pholder);
						signerExists = true;
					}
				}

				/*IF signer is not repeated the create a new signer and add it to the right placeholder name*/
				if (signerExists == false) {
					if (docLayoutData.enableSigningOrder == true) {
						signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
								.withFirstName(signersArray[j].getAt("signerFirstName"))
								.withLastName(signersArray[j].getAt("signerLastName"))
								.signingOrder(signersequence++).replacing(pholder);
						listSignerBuilders.add(signer);
					} else {
						signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
								.withFirstName(signersArray[j].getAt("signerFirstName"))
								.withLastName(signersArray[j].getAt("signerLastName"))
								.replacing(pholder);
						listSignerBuilders.add(signer);
					}
				}

				if ((docLayoutData.containsKey(signersArray[j].getAt("noteToSigner")))||(StringUtils.isNotEmpty(signersArray[j].getAt("noteToSigner")))) {
					signer.withEmailMessage(signersArray[j].getAt("noteToSigner")); //add note to the signer
				}
				package1.withSigner(signer); //add signer to the package
				numSigners = numSigners + signersArray.size();
			}
			package1.withDocument(mydoc);
			tmpPDFFile.delete();
		}
		/*Step 4 - Iterate through the documents map loop--End*/

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

			/*Step 8 and Step 9 - Retrieve package documents and apply individual layouts*/
			List<com.silanis.esl.sdk.Document> packageDocuments= dssEslClient.getPackage(packageId).getDocuments();
			for (int k = numOfAttachments+1; k < packageDocuments.size(); k++) {
				docId = packageDocuments[k].getId().toString();
				dssEslClient.getLayoutService().applyLayout(packageId, docId, listLayoutIds[k-numOfAttachments-1]);
			}

			/*Steps 10, 11, 12 - Remove the temporary placeholder emails and replace it with the actual email -Start*/
			packOps.signerSubstitute(packageId, numOfAttachments, completePackage);
			/*Steps 10, 11, 12 - Remove the temporary placeholder emails and replace it with the actual email -End*/

			/*Step 13 - Remove the placeholders which do not have any signer information associated with them. 
			 * This is to get rid of optional signers which do not have to sign*/
			for(Signer s: dssEslClient.getPackage(packageId).getPlaceholders())
			{
				dssEslClient.getPackageService().removeSigner(packageId, s.getId());
			}


			System.out.println(packageId.toString());

			if(numSigners < dssEslClient.getPackage(packageId).getPlaceholders().size()){
				messageMap = exceptionHandlerService.parseValidationErrors("568");
				return messageMap
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
			log.error(e);
			messageMap = exceptionHandlerService.parseException(e);
		}
		return messageMap


	}

	/**
	 * 
	 * @param data Map with original data that needs to be validated
	 * @return messageMap is empty if no errors or contains error message if there are any validation errors
	 */
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
				
				if ((data.containsKey(signersArray[j].getAt("placeHolderName")))||(StringUtils.isEmpty(signersArray[j].getAt("placeHolderName")))) {
					messageMap = exceptionHandlerService.parseValidationErrors("570");
				}
			}
		}
		return messageMap;
	}
}
