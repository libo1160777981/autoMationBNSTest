package com.expedia.testBNS.common;

import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.expedia.testBNS.util.Evoroment;

@Component
public class AddNotification  extends BaseSend{

	
	public AddNotification() {
		super();
		httpBase = new HttpPost();
	}

	public JSONObject addNotification(JSONObject json) throws Exception {
		seturl();
		setNewHead();
		if(json!=null){
			setBodyByJSONObj(json);
		}else{
			reader = new JSONReader(new FileReader("source/NotificationAddV1Request.json"));
			setBody("0001");
		}
		
		JSONObject jsonO = execute(httpBase);
		return jsonO;
		
	}



	private void seturl() throws URISyntaxException{
		URI uri = new URIBuilder()
		        .setScheme("http")
		        .setHost(evoroment.toString())
		        .setPath("/bns/v1/notifications")
		        .build();
		httpBase.setURI(uri);
	}
	
	private void setNewHead(){
		httpBase.setHeader("Client-ID","EPS");
		httpBase.setHeader("User-ID","33");
		httpBase.setHeader("TPID","33");
	}
	

}
