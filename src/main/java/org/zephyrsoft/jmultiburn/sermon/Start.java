package org.zephyrsoft.jmultiburn.sermon;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Start {
	
	public static void main(String[] args) {
		@SuppressWarnings({"unused", "resource"})
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
	}
	
}
