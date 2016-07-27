package com.expedia.testBNS;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.expedia.testBNS.common.SearchNotification;


public class SearchNitificationTest extends BaseTest{

	@Autowired
	SearchNotification searchNotification;
	JdbcTemplate jdbcTemplate;
	
	@Before
	public void start(){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//成功
	@Test
	public void testSuccess() throws Exception {
		//睡眠60秒
		//Thread.sleep(1000*60);
		JSONObject backdata = searchNotification.searchNotification(null);
		System.out.println(backdata);
		assertNotNull(backdata.getJSONArray("NotificationInfoList"));
	}





}
