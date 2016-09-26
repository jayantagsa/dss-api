package gov.gsa.dss.helper

class EmailContent {
	YamlConfig yamlConfig = new YamlConfig();

	String receiverEmail = yamlConfig.getDssSupportEmail();
	public void emailOnQueueExp(String eventOccurred, String packageName, String packageIdString) {
		String emailSubject = "DSS Active MQ Exception";
		String emailContent = "Could not update JMS queue";
	}
	public void emailOnRoutingExp(String eventOccurred, String packageName, String packageIdString) {
		String emailSubject = "DSS Callback Routing failed";
		String emailContent = "Could not route organization name to the right callback on event $eventOccurred with package name $packageName and id $packageIdString." ;
		println emailContent;
//		TODO: send email module
		
	}
}
