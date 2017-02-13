package gov.gsa.dss.helper

import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient
import gov.gsa.dss.helper.staic.OrgCodes

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder
import org.apache.commons.lang3.RandomStringUtils
import java.text.Normalizer
import java.text.Normalizer.Form

class FileOperations {
	final static Logger log =Logger.getLogger(FileOperations.class);
	/**
    *
    *
    * @param encodedString
    */
   InputStream decodeBase64String(String encodedString) {
       BASE64Decoder decoder = new BASE64Decoder();
	   String normalizedEncodedString = Normalizer.normalize(encodedString, Normalizer.Form.NFKC);
	   byte[] decodedBytes = decoder.decodeBuffer(normalizedEncodedString);
       InputStream bufferedInputStream = new ByteArrayInputStream(decodedBytes);
       return bufferedInputStream;
   }
   
   private File writePDFFileToLocalDisk(InputStream inputStream) {
	   String ext = "pdf"
	   String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), ext);
	   String fullyQualifiedFiledNameWOSlash = System.getProperty("java.io.tmpdir") + name
	   String normalizedFileName = Normalizer.normalize(fullyQualifiedFiledNameWOSlash, Normalizer.Form.NFKC);

	   OutputStream outputStream = null

	   try {
		   // write the inputStream to a FileOutputStream
		   outputStream = new FileOutputStream(new File(normalizedFileName))

		   int read = 0
		   byte[] bytes = new byte[1024]

		   while ((read = inputStream.read(bytes)) != -1) {
			   outputStream.write(bytes, 0, read)
		   }
	   } catch (IOException e) {
		   log.error(e);
	   } finally {
		   if (inputStream != null) {
			   try {
				   inputStream.close()
			   } catch (IOException e) {
			   log.error(e);
				   //e.printStackTrace()
			   }
		   }
		   if (outputStream != null) {
			   try {
				   // outputStream.flush()
				   outputStream.close()
			   } catch (IOException e) {
				   log.error(e);
			   }

		   }
	   }
	   new File(normalizedFileName)
   }

}
