package gov.gsa.controller;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.RandomAccessFileOrArray
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy
import com.silanis.esl.api.util.EmailValidator
import com.silanis.esl.sdk.*
import com.silanis.esl.sdk.builder.*
import com.silanis.esl.sdk.internal.EslServerException
import com.silanis.esl.sdk.service.*
import gov.gsa.dss.helper.ExceptionHandlerService
import gov.gsa.dss.helper.ResponseBuilder
import gov.gsa.dss.helper.Authenticator
import gov.dss.sdk.service.*
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import sun.misc.BASE64Decoder

import java.nio.file.Path

import static com.silanis.esl.sdk.builder.DocumentPackageAttributesBuilder.newDocumentPackageAttributes
import static com.silanis.esl.sdk.builder.PackageBuilder.newPackageNamed
import static com.silanis.esl.sdk.builder.SignatureBuilder.*
import static com.silanis.esl.sdk.builder.SignerBuilder.newSignerWithEmail

import sun.misc.BASE64Decoder;

public class CreatePackageController {
	
	/**
    *
    *
    * @param signatureInsertionData
    */
   Map<String, Object> dssUniversalConnector(Map<String, Object> signatureInsertionData) {
       int FIELD_WIDTH = 145
       int FIELD_HEIGHT = 45
       int occurrences1 = 0;
       String filePath = null;
       PdfReader pdfReader = null;
       String type = null;
       SignatureBuilder signBuild;
       String fileName = null;
       SignerBuilder signer;
       String docName = null;
       Path p = null;
       def messageList = [];
       Map<String, String> messageMap = new HashMap<String, String>();
       ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
       ResponseBuilder responseBuilder = new ResponseBuilder();
       String successMessage;
	   
	   Authenticator auth = new Authenticator();
	   EslClient dssEslClient = auth.getAuth();

       /*Validate the Map signatureInsertionData that comes in.*/
       messageMap = validateData(signatureInsertionData);

       if (!(messageMap==null)) {
           return messageMap
       }

       /*Create the Package.*/
       PackageBuilder package1 = newPackageNamed(signatureInsertionData.packageName);
       package1.withSenderInfo(SenderInfoBuilder.newSenderInfo(signatureInsertionData.senderEmail));

       def documentsMap = signatureInsertionData.documents;
       for (int i = 0; i < documentsMap.size(); i++) {
           docName = (documentsMap[i].getAt("document").getAt("documentName"));

           /*Convert base64 encoded file String into InputStream*/
           InputStream bufferedInputStream = null;
           try {
               bufferedInputStream = decodeBase64String(documentsMap[i].getAt("document").getAt("documentContent"));
           }
           catch (Exception e) {
               /*This is when the base64 encoded file is corrupt and ends up with an exception while decoding it*/
               messageMap = exceptionHandlerService.parseValidationErrors("File could not be decoded.",
                       564,
                       "Validation Error");
               return messageMap
           }
           File tmpPDFFile = writePDFFileToLocalDisk(bufferedInputStream)
           filePath = tmpPDFFile.getCanonicalPath()
           bufferedInputStream.close()

           Document mydoc = DocumentBuilder.newDocumentWithName(docName)
                   .fromFile(filePath)
                   .build();
           def signersArray = documentsMap[i].getAt("document").getAt("signers");
           for (int j = 0; j < signersArray.size(); j++) {
               type = signersArray[j].getAt("signType");
               occurrences1 = 0;

               switch (type) {
                   case "ClickToInitial":
                       signBuild = initialsFor(signersArray[j].getAt("signerEmail"));
                       break;
                   case "ClickToSign":
                       signBuild = signatureFor(signersArray[j].getAt("signerEmail"));
                       break;
                   case "CaptureSignature":
                       signBuild = captureFor(signersArray[j].getAt("signerEmail"));
                       break;
                   case "MobileCapture":
                       signBuild = mobileCaptureFor(signersArray[j].getAt("signerEmail"));
                       break;
               }

               if (StringUtils.isNotEmpty(signersArray[j].getAt("searchText"))) {
                   pdfReader = new PdfReader(new RandomAccessFileOrArray(filePath), null);

                   for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
                       String currentPageText = null;
                       try {
                           SimpleTextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                           currentPageText = PdfTextExtractor.getTextFromPage(pdfReader, page, strategy);
                       } catch (Exception e) {
                           e.printStackTrace()
                       }
                       while (currentPageText.contains(signersArray[j].getAt("searchText"))) {
                           occurrences1++;
                           currentPageText = currentPageText.substring(currentPageText.indexOf(signersArray[j].getAt
                                   ("searchText")) + signersArray[j].getAt("searchText").length());
                       }
                   }
                   if (occurrences1 == 0) {
                       /*If the search text does not exists in the document then an error message shoudl be displayed.
                       The package should only be created in draft mode with the signer info.*/
//                       signatureInsertionData.packageOption = "create";
                       messageMap = exceptionHandlerService.parseValidationErrors("Search Text not found in the document.",
                                                                                   563,
                                                                                   "Validation Error");
                       return messageMap

                   }
                   pdfReader.close();
               }
               if (occurrences1 > 0) {
                   TextAnchorBuilder textAnchorBuilder = TextAnchorBuilder.newTextAnchor(signersArray[j].getAt
                           ("searchText"));
                   for (int k = 0; k < occurrences1; k++) {
                       mydoc.getSignatures().add(signBuild
                               .withPositionAnchor(textAnchorBuilder
                               .atPosition(textAnchorPosition(signersArray[j].getAt("signaturePosition")))
                               .withSize(FIELD_WIDTH, FIELD_HEIGHT)
                               .withOffset(0, 0)
                               .withCharacter(2)
                               .withOccurence(k)
                       ).build());

                   }
                   mydoc.setIndex(1)
               }
               if (signatureInsertionData.enableSigningOrder == true) {
                   signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
                           .withFirstName(signersArray[j].getAt("signerFirstName"))
                           .withLastName(signersArray[j].getAt("signerLastName"))
                           .signingOrder(j);
               } else {
                   signer = SignerBuilder.newSignerWithEmail(signersArray[j].getAt("signerEmail"))
                           .withFirstName(signersArray[j].getAt("signerFirstName"))
                           .withLastName(signersArray[j].getAt("signerLastName"));
               }
			   
				if ((signatureInsertionData.containsKey(signersArray[j].getAt("noteToSigner")))||(StringUtils.isNotEmpty(signersArray[j].getAt("noteToSigner")))) {
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
                   .withAttribute("orgName", signatureInsertionData.orgName)
                   .build())
                   .build();
			
			/*Add description to the package*/
			if ((signatureInsertionData.containsKey(signatureInsertionData.packageDescription))||(StringUtils.isNotEmpty(signatureInsertionData.packageDescription))) {
				completePackage.setDescription(signatureInsertionData.packageDescription);
			}
           PackageId packageId = dssEslClient.createPackage(completePackage);

           if (signatureInsertionData.packageOption == "createSend") {
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

   /**
    *
    * Validate the dssUniversalConnector data
    * @param signatureInsertionData
    */

   def validateData(Map<String, Object> signatureInsertionData) {
       def messageList = [];
       EmailValidator validator = new EmailValidator();
       String fileName;
       int nameLength;
       String extension;
       ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();
       def messageMap;

       if ((signatureInsertionData.containsKey(signatureInsertionData.orgName))||(StringUtils.isEmpty(signatureInsertionData.orgName))) {
           messageMap = exceptionHandlerService.parseValidationErrors("550");
       }
       if ((signatureInsertionData.containsKey(signatureInsertionData.packageName))||(StringUtils.isEmpty(signatureInsertionData.packageName))) {
           messageMap = exceptionHandlerService.parseValidationErrors("540");
       }

           if ((signatureInsertionData.containsKey(signatureInsertionData.senderEmail))||(StringUtils.isEmpty(signatureInsertionData.senderEmail))) {
               messageMap = exceptionHandlerService.parseValidationErrors("541");
           } else {
               if (validator.valid(signatureInsertionData.senderEmail) == false) {
                   messageMap = exceptionHandlerService.parseValidationErrors("542");
               }
           }


           /*This is a Boolean type variable so will need to check for null value and not 'if String empty'.
           If the user does not provide a value for 'enableSigningOder' then it will be assigned a default value of
           'false'*/
           if ((signatureInsertionData.containsKey(signatureInsertionData.enableSigningOrder))||(signatureInsertionData.enableSigningOrder == null)) {
               signatureInsertionData.enableSigningOrder = false;
           }

           if ((signatureInsertionData.containsKey(signatureInsertionData.packageOption))||(StringUtils.isEmpty(signatureInsertionData.packageOption))) {
               messageMap = exceptionHandlerService.parseValidationErrors("543");
           }

           def documentsMap = signatureInsertionData.documents;
           for (int i = 0; i < documentsMap.size(); i++) {
               if ((signatureInsertionData.containsKey(documentsMap[i].getAt("document").getAt("documentName")))||(StringUtils.isEmpty(documentsMap[i].getAt("document").getAt("documentName")))) {
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

               if ((signatureInsertionData.containsKey(documentsMap[i].getAt("document").getAt("documentContent")))||(StringUtils.isEmpty(documentsMap[i].getAt("document").getAt("documentContent")))) {
                   /*TODO Verify that the file is of type PDF*/
                   messageMap = exceptionHandlerService.parseValidationErrors("545");

               }
               def signersArray = documentsMap[i].getAt("document").getAt("signers");
               for (int j = 0; j < signersArray.size(); j++) {

                   if ((signatureInsertionData.containsKey(signersArray[j].getAt("signerEmail")))||(StringUtils.isEmpty(signersArray[j].getAt("signerEmail")))) {
                       messageMap = exceptionHandlerService.parseValidationErrors("546");
                   } else {
                       if (validator.valid(signersArray[j].getAt("signerEmail")) == false) {
                           messageMap = exceptionHandlerService.parseValidationErrors("547");
                       }
                   }


                   if ((signatureInsertionData.containsKey(signersArray[j].getAt("signerFirstName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerFirstName")))) {
                       messageMap = exceptionHandlerService.parseValidationErrors("548");
                   }

                   if ((signatureInsertionData.containsKey(signersArray[j].getAt("signerLastName")))||(StringUtils.isEmpty(signersArray[j].getAt("signerLastName")))) {
                       messageMap = exceptionHandlerService.parseValidationErrors("549");
                   }
                   /*If signature position is not given by user then default it to TOP_RIGHT.*/
                   if ((signatureInsertionData.containsKey(signersArray[j].getAt("signaturePosition")))||(StringUtils.isEmpty(signersArray[j].getAt("signaturePosition")))) {
                       signersArray[j].putAt("signaturePosition", "TOPRIGHT");
                   }

                   /*If the user gives a packageOption of createSend but does not give a search text then send a validation error message*/
                   if ((signatureInsertionData.packageOption == "createSend") && (StringUtils.isEmpty(signersArray[j].getAt("searchText")))) {
                       messageMap = exceptionHandlerService.parseValidationErrors("561");
                   }

                   /*If the user does not provide a value for 'signType' then it will be assigned a default value of 'signatureFor'*/
                   if ((signatureInsertionData.containsKey(signersArray[j].getAt("signType")))||(StringUtils.isEmpty(signersArray[j].getAt("signType")))) {
                       signersArray[j].putAt("signType", "signatureFor");
                   }
               }
           }
       return messageMap;
   }

   /**
    *
    *
    * @param encodedString
    */
   InputStream decodeBase64String(String encodedString) {
       BASE64Decoder decoder = new BASE64Decoder();
       byte[] decodedBytes = decoder.decodeBuffer(encodedString);
       InputStream bufferedInputStream = new ByteArrayInputStream(decodedBytes);
       return bufferedInputStream;
   }

   /**
    *
    * This method is used to convert signationPosition string to TextAnchorPosition enum.
    * @param signPosition
    */
   TextAnchorPosition textAnchorPosition(String signPosition) {
       TextAnchorPosition myanchor;
       switch (signPosition.toUpperCase()) {
           case "BOTTOMRIGHT":
               myanchor = TextAnchorPosition.BOTTOMRIGHT;
               break;
           case "BOTTOMLEFT":
               myanchor = TextAnchorPosition.BOTTOMLEFT;
               break;
           case "TOPRIGHT":
               myanchor = TextAnchorPosition.TOPRIGHT;
               break;
           case "TOPLEFT":
               myanchor = TextAnchorPosition.TOPLEFT;
               break;
       }
       return myanchor;
   }
   
   private File writePDFFileToLocalDisk(InputStream inputStream) {
	   String ext = "pdf"
	   String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), ext);
	   String fullyQualifiedFiledName = System.getProperty("java.io.tmpdir") + File.separator + name

	   OutputStream outputStream = null

	   try {
		   // write the inputStream to a FileOutputStream
		   outputStream = new FileOutputStream(new File(fullyQualifiedFiledName))

		   int read = 0
		   byte[] bytes = new byte[1024]

		   while ((read = inputStream.read(bytes)) != -1) {
			   outputStream.write(bytes, 0, read)
		   }
	   } catch (IOException e) {
		   e.printStackTrace()
	   } finally {
		   if (inputStream != null) {
			   try {
				   inputStream.close()
			   } catch (IOException e) {
				   e.printStackTrace()
			   }
		   }
		   if (outputStream != null) {
			   try {
				   // outputStream.flush()
				   outputStream.close()
			   } catch (IOException e) {
				   e.printStackTrace()
			   }

		   }
	   }
	   new File(fullyQualifiedFiledName)
   }


}
