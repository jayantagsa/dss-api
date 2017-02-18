package gov.gsa.dss.test;

import java.io.FileNotFoundException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Test;

import gov.gsa.dss.helper.Mail;
import gov.gsa.dss.helper.staic.EmailMessages;

import static org.junit.Assert.*;

public class MailTest {
	final static Logger log =Logger.getLogger(MailTest.class);

	@Test
	public void testMailModule() {
		String testSuccess = "";
		Mail MailSender;
		try {
			MailSender = new Mail();
			MailSender.sendMail("jayanta.sinha@gsa.gov", "jsinha@valiantsolutions.com",
					EmailMessages.getSubject("testsuccess"), EmailMessages.getSubject("testsuccess"));
			testSuccess = "success";

		} catch (FileNotFoundException e) {
			log.error(e);
			testSuccess = "failed";
		} catch (NamingException e) {
			log.error(e);
			testSuccess = "failed";
		} catch (JSONException e) {
			log.error(e);
			testSuccess = "failed";
		}
		assertEquals("Testsuccess = success", "success", testSuccess);
	}
}
