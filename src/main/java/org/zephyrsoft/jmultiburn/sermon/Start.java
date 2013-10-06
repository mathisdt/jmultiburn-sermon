package org.zephyrsoft.jmultiburn.sermon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Start {
	
	private static final Logger LOG = LoggerFactory.getLogger(Start.class);
	
	public static void main(String[] args) {
		LOG.info("loading application context");
		@SuppressWarnings({"unused", "resource"})
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
	}
	
}
