package gov.gsa.dss.test;

import java.io.FileNotFoundException;

import javax.naming.NamingException;

import org.junit.Test;

import gov.gsa.dss.helper.Mail;
import static org.junit.Assert.*;
public class MailTest {
@Test
public void testMailModule()
{
	String testSuccess="";
	Mail MailSender;
	try {
		MailSender = new Mail();
		MailSender.sendMail("jayanta.sinha@gsa.gov", "jsinha@valiantsolutions.com", "success");
		testSuccess="success";
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		testSuccess="failed";
	} 
	assertEquals("Testsuccess = success", "success", testSuccess);
}
}
