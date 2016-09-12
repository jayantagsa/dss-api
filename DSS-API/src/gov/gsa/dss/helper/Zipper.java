package gov.gsa.dss.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.silanis.esl.sdk.Document;
import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
public class Zipper {
	
	public  byte[] getZip(DocumentPackage docPackage,EslClient esl) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		List <Document> Documents = docPackage.getDocuments();
		for (Document doc : Documents )
		{
			System.out.println(doc.getName());
			ZipEntry entry = new ZipEntry(doc.getName()+".pdf");
			
			entry.setSize(esl.downloadDocument(docPackage.getId(), doc.getId()+"").length);
			zos.putNextEntry(entry);
			zos.write(esl.downloadDocument(docPackage.getId(), doc.getId()+""));
			zos.closeEntry();
		}
		ZipEntry entry = new ZipEntry("Evidence.pdf");
		
		entry.setSize(esl.downloadEvidenceSummary(docPackage.getId()).length);
		zos.putNextEntry(entry);
		zos.write(esl.downloadEvidenceSummary(docPackage.getId()));
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}
	public  byte[] getUnZip(byte[] data) throws IOException, DataFormatException 
	
	{
	      // Create the decompressor and give it the data to compress 
        Inflater decompressor = new Inflater(); 
        decompressor.setInput(data, 0, data.length); 
 
        // Create an expandable byte array to hold the decompressed data 
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length); 
 
        // Decompress the data 
        byte[] buf = new byte[1024]; 
        while (!decompressor.finished()) 
        { 
            int count = decompressor.inflate(buf); 
            bos.write(buf, 0, count); 
        } 
        bos.close(); 
 
        // Get the decompressed data 
        return bos.toByteArray(); 
	}
	/*public static byte[] zipBytes(String filename, byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry(filename);
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}*/
}
