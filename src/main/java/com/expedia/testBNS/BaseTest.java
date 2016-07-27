package com.expedia.testBNS;

import javax.sql.DataSource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.expedia.testBNS.util.StandaloneDataConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = StandaloneDataConfig.class)
public abstract class BaseTest {
	@Autowired
	DataSource dataSource;

}
