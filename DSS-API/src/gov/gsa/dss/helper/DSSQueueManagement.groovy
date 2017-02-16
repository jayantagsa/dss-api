package gov.gsa.dss.helper;

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.log4j.Logger;

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

/**
 * Created by nphadke on 8/4/2016 .
 * Migrated to Lean Framework by sudhangi on 9/19/2016.
 */
    public class DSSQueueManagement {
		final static Logger log =Logger.getLogger(DSSQueueManagement.class);
		YamlConfig yamlConfig = new YamlConfig();
		private ConnectionFactory factory = null;
        private Connection connection = null;
        Map<String, String> messageMap = new HashMap<String, String>();
        ResponseBuilder responseBuilder = new ResponseBuilder();
        ExceptionHandlerService exceptionHandlerService = new ExceptionHandlerService();


        public Map<String, Object> initConnection(){
            try {
                factory = new ActiveMQConnectionFactory(yamlConfig.getJmsBrokerUrl());
                connection = factory.createConnection();
				/*Unchecked Return Value to NULL Pointer Dereference Solution*/
				if (connection != null) {
	                connection.start();
	                messageMap = responseBuilder.buildSuccessResponse("DSS Queue initialization complete.")
				}
            }
            catch (JMSException jmsExp) {
				connection.close();
                messageMap = exceptionHandlerService.parseException(jmsExp);
            }
            return messageMap;
        }

        public Map<String, Object> closeConnection(){
            try {
                connection.close();
                messageMap = responseBuilder.buildSuccessResponse("DSS Queue connection closed.")
            }
            catch (JMSException jmsExp) {
                messageMap = exceptionHandlerService.parseException(jmsExp);
            }
            return messageMap;
        }

        public Map<String, Object> publishToQueue(String queueName, String messageString){
            Session session = null;
            Destination destination = null;
            MessageProducer producer = null;

		try {
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
			/*Unchecked Return Value to NULL Pointer Dereference Solution*/
			if (session != null) {
				destination = session.createQueue(queueName);
				producer = session.createProducer(destination);
				/*Unchecked Return Value to NULL Pointer Dereference Solution*/
				if (producer != null) {
					TextMessage textMessage = session.createTextMessage();
					/*Unchecked Return Value to NULL Pointer Dereference Solution*/
					if (textMessage != null) {
						textMessage.setText(messageString);
						producer.send(textMessage);
						session.close();
						log.info("Sent: " + textMessage.getText());
						messageMap = responseBuilder.buildSuccessResponse(textMessage.getText())
					}
				}
			}
		}
            catch(JMSException jmsExp){
				session.close();
                messageMap = exceptionHandlerService.parseException(jmsExp);
            }
            return messageMap;
        }

        public Map<String, Object> retrieveFromQueue(String queueName){
            Session session = null;
            Destination destination = null;
            MessageConsumer consumer = null;
            TextMessage text = null;
            Message message = null;

            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				/*Unchecked Return Value to NULL Pointer Dereference Solution*/
			if (session != null) {
				destination = session.createQueue(queueName);
				consumer = session.createConsumer(destination);
				/*Unchecked Return Value to NULL Pointer Dereference Solution*/
				if (consumer != null) {
					message = consumer.receive();

					if (message instanceof TextMessage)
						text = (TextMessage) message;
					log.info("Received : " + text.getText());
					consumer.close();
				}
				session.close();
				messageMap = responseBuilder.buildSuccessResponse(text.getText())
			}
            }
            catch(JMSException jmsExp){
                messageMap = exceptionHandlerService.parseException(jmsExp);
            }
            return messageMap;
        }

    }
