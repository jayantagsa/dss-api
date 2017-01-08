package gov.gsa.dss.helper

import org.apache.log4j.Logger;

class EmailContent {
	final static Logger log =Logger.getLogger(EmailContent.class);
	YamlConfig yamlConfig = new YamlConfig();

	String receiverEmail = yamlConfig.getDssSupportEmail();
	public void emailOnQueueExp(String eventOccurred, String packageName, String packageIdString) {
		String emailSubject = "DSS Active MQ Exception";
		String emailContent = "Could not update JMS queue";
	}
	public void emailOnRoutingExp(String eventOccurred, String packageName, String packageIdString) {
		String emailSubject = "DSS Callback Routing failed";
		String emailContent = "Could not route organization name to the right callback on event $eventOccurred with package name $packageName and id $packageIdString." ;
		log.info( emailContent);
//		TODO: send email module
		
	}
}
