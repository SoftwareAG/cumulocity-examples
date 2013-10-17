package com.cumulocity.sdk.agent;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MpsAgentMain {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("META-INF/spring/mps.agent.application.context.xml");
	}
	
}
