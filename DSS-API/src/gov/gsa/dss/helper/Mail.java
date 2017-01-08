package gov.gsa.dss.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class Mail {
	final static Logger log =Logger.getLogger(Mail.class);
	protected YamlConfig configObj;
	protected Properties props;

	public Mail() throws FileNotFoundException, NamingException {
		configObj = new YamlConfig();
		props = System.getProperties();
	}

  

	public boolean sendMail(String senderEmailAddr, String receiverEmailAddr, String msgBody, String msgSubject) {
		// Recipient's email ID needs to be mentioned.
		try {
			int i = 0;
			log.info(configObj.getProp("smtp"));
			props.put("mail.smtp.host", configObj.getProp("smtp"));
			log.info(i++);
			Session session = Session.getInstance(props, null);
			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom(new InternetAddress(senderEmailAddr, senderEmailAddr));
			msg.setSubject(msgSubject, "UTF-8");
			msg.setText(msgBody, "UTF-8");
			log.info(i++);
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmailAddr, false));
			Transport.send(msg);
			log.info("EMail Sent Successfully!!");
			return true;
		} catch (AddressException e) {
			log.error(e);
			return false;
		} catch (MessagingException e) {
			log.error(e);
				return false;
		} catch (IOException e) {
			log.error(e);
			return false;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}
}