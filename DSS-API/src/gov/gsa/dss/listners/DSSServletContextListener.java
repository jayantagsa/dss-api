package gov.gsa.dss.listners;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import gov.gsa.dss.helper.EDMSQueueConsumer;
import gov.gsa.dss.helper.Mail;

/**
 * 
 * @author jayantasinha
 *
 */
public class DSSServletContextListener implements ServletContextListener {
	final static Logger log =Logger.getLogger(DSSServletContextListener.class);
	Thread thread =null;
	@SuppressWarnings("deprecation")
	/**
	 * Kills ActiveMQ threads when application context stops
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for(Thread t:threadArray) {
			log.info(t.getName());
			if(t.getName().contains("ActiveMQ")) {
				synchronized(t) {
					t.stop(); //don't complain, it works
				}
			}
		}
	}
	
	/**
	 * Starts ActiveMQ EDMS subscription
	 */
	@Override
	
	public void contextInitialized(ServletContextEvent arg0) {
		// do all the tasks that you need to perform just after the server starts
		//Notification that the servlet context is about to be shut down.  


		EDMSQueueConsumer consumer =new EDMSQueueConsumer();
		thread = new Thread(consumer);
		thread.start();


		//consumer.run();
	}

}