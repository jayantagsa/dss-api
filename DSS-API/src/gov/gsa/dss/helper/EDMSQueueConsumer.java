package gov.gsa.dss.helper;
import java.io.FileNotFoundException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.gsa.dss.controller.EDMSController;

public class EDMSQueueConsumer implements Runnable{
	
	final static Logger log =Logger.getLogger(EDMSQueueConsumer.class);
	// Name of the topic from which we will receive messages from = " DSS_EDMS_QUEUE_DEV"
	//Starts new thread
	public void run() {
		// Getting JMS connection from the server
		try {
			Connection connection;
			YamlConfig yamlConfig = new YamlConfig();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(yamlConfig.getJmsBrokerUrl());
			MessageConsumer consumer;
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false,
					Session.CLIENT_ACKNOWLEDGE);
			consumer = session.createConsumer(session.createQueue("DSS_EDMS_QUEUE_DEV"));
			MessageListener listner = new MessageListener() {
				EDMSController edmsController = new EDMSController();
				public void onMessage(Message message) {
					try {
						if (message instanceof TextMessage) {
							TextMessage textMessage = (TextMessage) message;
							log.info("Received message:\t"
									+ textMessage.getText() + "'");
							JSONObject jmsText = new JSONObject(textMessage.getText());
							edmsController.packageUpload(jmsText.get("packageId")+"", jmsText.get("orgName")+"");
							message.acknowledge();
						}
					} catch (JMSException e){
						
						log.error(e);
					} catch (JSONException e){
						log.error(e);
					}
					catch (Exception e){
						log.error(e);
					}
				}
			};
			consumer.setMessageListener(listner);

			/*try {
				//System.in.read();
			} catch (IOException e) {
				log.error(e);
			}*/
			connection.close();
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			log.error(e1);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			log.error(e1);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			log.error(e1);
		}
	}
	
}    