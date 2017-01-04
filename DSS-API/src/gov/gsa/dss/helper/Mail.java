package gov.gsa.dss.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.NamingException;

<<<<<<< HEAD
import org.apache.log4j.Logger;

import gov.gsa.dss.helper.staic.EmailMessages;

import javax.activation.*;

=======
>>>>>>> refs/heads/DSS-Sprint22_518_merge
public class Mail {
	final static Logger log =Logger.getLogger(Mail.class);
	protected YamlConfig configObj;
	protected Properties props;

	public Mail() throws FileNotFoundException, NamingException {
		configObj = new YamlConfig();
		props = System.getProperties();
	}

<<<<<<< HEAD
   public boolean sendMail(String senderEmailAddr, String receiverEmailAddr, String msgBody, String msgSubject)
   {    
      // Recipient's email ID needs to be mentioned.
	   try
	    {

		    int i=0;
		    log.info(configObj.getProp("smtp"));
		  props.put("mail.smtp.host", configObj.getProp("smtp"));
		  //System.out.println(i++);
		  Session session = Session.getInstance(props, null);
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(senderEmailAddr, senderEmailAddr));

	      //msg.setReplyTo(InternetAddress.parse(strReciever, false));

	      msg.setSubject(msgSubject, "UTF-8");

	      msg.setText(msgBody, "UTF-8");
	      i++;
	     // System.out.println(i++);

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmailAddr, false));
	      Transport.send(msg);  

	      log.info("EMail Sent Successfully!!");
	      return true;
	    }
	    catch (Exception e) {
	      log.error(e);
	      return false;
	    }
   }
=======
	public boolean sendMail(String senderEmailAddr, String receiverEmailAddr, String msgBody, String msgSubject) {
		// Recipient's email ID needs to be mentioned.
		try {
			int i = 0;
			System.out.println(configObj.getProp("smtp"));
			props.put("mail.smtp.host", configObj.getProp("smtp"));
			System.out.println(i++);
			Session session = Session.getInstance(props, null);
			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom(new InternetAddress(senderEmailAddr, senderEmailAddr));
			msg.setSubject(msgSubject, "UTF-8");
			msg.setText(msgBody, "UTF-8");
			System.out.println(i++);
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmailAddr, false));
			Transport.send(msg);
			System.out.println("EMail Sent Successfully!!");
			return true;
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
>>>>>>> refs/heads/DSS-Sprint22_518_merge
}