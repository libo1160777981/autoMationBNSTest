package com.expedia.testBNS.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.expedia.testBNS.util.StandaloneDataConfig;


@Component
public class CreateClassByTablename {
	
	private String tableName = "notification";
	private String sql = "SELECT syscolumns.name as _name,systypes.name as _type,syscolumns.isnullable,syscolumns.length FROM syscolumns, systypes WHERE syscolumns.xusertype = systypes.xusertype AND syscolumns.id = object_id('"+tableName+"')";

	@Autowired
	DataSource dataSource;
	
	JdbcTemplate jdbcTemplate;
	
	private String SQLTYPE = "sqlserver";
	
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(StandaloneDataConfig.class);
		CreateClassByTablename createClassByTablename = ctx.getBean(CreateClassByTablename.class);
		createClassByTablename.create();
	}
	
	public void create() throws FileNotFoundException {
		System.out.println(upperFirstChar(tableName));
		
		JSONObject map = getMap(SQLTYPE);
		List<Map<String, Object>> types = getTableColum();
		for(Map<String, Object> m : types){
			StringBuilder filed = new StringBuilder("private ");
			filed.append(map.get(m.get("_type")));
			filed.append(" ");
			filed.append(lowcaseFirstChar(m.get("_name").toString()));
			filed.append(";");
			System.out.println(filed.toString());
		}

	}
	

	private JSONObject getMap(String sqltype) throws FileNotFoundException{
		JSONReader reader = new JSONReader(new FileReader("source/SqlTypeMapJavaType.json"));
		return reader.readObject(JSONObject.class).getJSONObject(sqltype);
	}
	
	private List<Map<String, Object>> getTableColum(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		return  jdbcTemplate.queryForList(sql);
	}
	
	private String lowcaseFirstChar(String str){
		return str.replaceFirst(String.valueOf(str.charAt(0)), String.valueOf((char)(str.charAt(0)+32)));
	}
	private String upperFirstChar(String str){
		return str.replaceFirst(String.valueOf(str.charAt(0)), String.valueOf((char)(str.charAt(0)-32)));
	}

}
