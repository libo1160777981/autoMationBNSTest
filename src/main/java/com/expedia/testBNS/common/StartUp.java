package com.expedia.testBNS.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.expedia.testBNS.util.StandaloneDataConfig;

public class StartUp {

	 static ApplicationContext ctx =  new AnnotationConfigApplicationContext(StandaloneDataConfig.class);
	
	public static void main(String[] args) throws Exception {
		CreateClassByTablename createClassByTablename = ctx.getBean(CreateClassByTablename.class);
		createClassByTablename.create();
	}

}
