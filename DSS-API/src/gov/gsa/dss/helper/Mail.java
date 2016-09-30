package gov.gsa.dss.helper;

import java.io.FileNotFoundException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.NamingException;

import gov.gsa.dss.helper.staic.EmailMessages;

import javax.activation.*;

public class Mail {
	protected YamlConfig configObj;
	Properties props;
	public Mail() throws FileNotFoundException, NamingException
	{
		configObj= new YamlConfig();
		props = System.getProperties();
	}

   public boolean sendMail(String strSender, String strReciever, String msgCode)
   {    
      // Recipient's email ID needs to be mentioned.
	   try
	    {
		   
		   
			
		    //String smtpHostServer = "smtp.gsa.gov";
		    //String emailID = "pankaj@journaldev.com";
		    
		    

		  props.put("mail.smtp.host", configObj.getProp("smtp"));

		  Session session = Session.getInstance(props, null);
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(strSender, strSender));

	      //msg.setReplyTo(InternetAddress.parse(strReciever, false));

	      msg.setSubject("DSS Blah Blah", "UTF-8");

	      msg.setText(EmailMessages.getMessage("success"), "UTF-8");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(strReciever, false));
	      System.out.println("Message is ready");
   	  Transport.send(msg);  

	      System.out.println("EMail Sent Successfully!!");
	      return true;
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      return false;
	    }
   }
}