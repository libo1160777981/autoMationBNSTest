package com.louli.JDBC;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MyJdbcTemplate {


	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/ibatis?useUnicode=true&characterEncoding=utf8");
		dataSource.setUsername("root");
		dataSource.setPassword("1qaz@WSX1qaz");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	public JdbcTemplate getJdbcTemplate() {
		setDataSource();
		return jdbcTemplate;
	}
	
	public static void main(String[] args) {
		MyJdbcTemplate yyJdbcTemplate = new MyJdbcTemplate();
		List<Map<String, Object>>  list = yyJdbcTemplate.getJdbcTemplate().queryForList("select * from ClientInfo");
		System.out.println(list);
	}

	

}
