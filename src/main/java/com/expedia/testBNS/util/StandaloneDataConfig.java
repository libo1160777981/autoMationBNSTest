package com.expedia.testBNS.util;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
@ComponentScan(basePackages = "com.expedia.testBNS") 
public abstract class StandaloneDataConfig {
	
	
	
	int DATASOURCE = 0;
	int REMOTEURL = 0;
	
	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		switch (DATASOURCE){
			case 0:
				dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				dataSource.setUrl("jdbc:sqlserver://***;database=Notification");
				dataSource.setUsername("louli");
				dataSource.setPassword("***");
				break;
		}
		
		return dataSource;
		
	}
	
	
	@Bean
	public Evoroment evoromentLocal(){
		Evoroment voroment = null;
		switch(REMOTEURL){
			case 0:
				voroment = new Evoroment("127.0.0.1:8888");
				break;
			case 1:
				voroment = new Evoroment("**.ngrok.io");
				break;
		}
		return voroment;
	}



}
