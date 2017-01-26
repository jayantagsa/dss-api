package gov.gsa.dss.listners;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import gov.gsa.dss.helper.QueueConsumer;

public class DSSServletContextListener implements ServletContextListener {
	
	 Thread thread =null;
	  @SuppressWarnings("deprecation")
	@Override
	  public void contextDestroyed(ServletContextEvent arg0) {
		  Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		  Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		  for(Thread t:threadArray) {
			  System.out.println(t.getName());
		      if(t.getName().contains("ActiveMQ")) {
		          synchronized(t) {
		              t.stop(); //don't complain, it works
		          }
		      }
		  }
	  }
	  @Override
	  public void contextInitialized(ServletContextEvent arg0) {
	    // do all the tasks that you need to perform just after the server starts
		  //Notification that the servlet context is about to be shut down.  
		  
		  
			  QueueConsumer consumer =new QueueConsumer();
			  thread = new Thread(consumer);
	        thread.start();
		 
		 
		//consumer.run();
	  }

	}