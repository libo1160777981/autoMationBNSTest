package com.expedia.testBNS.common;


import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.expedia.testBNS.util.Evoroment;

public class BaseSend {
	
	HttpEntityEnclosingRequestBase httpBase;
	JSONReader reader;
	
	@Autowired
	Evoroment evoroment;

	protected JSONObject execute(HttpEntityEnclosingRequestBase http) throws Exception {
		setHead();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		JSONObject jsonO = null;
		CloseableHttpResponse response = httpclient.execute(http);
		try {
			HttpEntity entity = response.getEntity();
			String entityString = EntityUtils.toString(entity);
			jsonO = JSON.parseObject(entityString);
		} finally {
			response.close();
		}
		return jsonO;
	}
	
	void setBody(String key) throws UnsupportedEncodingException{
		String str = reader.readObject(Map.class).get(key).toString();
		StringEntity stringEntity = new StringEntity(str);
		httpBase.setEntity(stringEntity);
	}
	
	void setBodyByJSONObj(JSONObject jSONObject) throws UnsupportedEncodingException{
		String str = jSONObject.toJSONString();
		StringEntity stringEntity = new StringEntity(str);
		httpBase.setEntity(stringEntity);
	}
	
	private void setHead(){
		httpBase.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
		httpBase.setHeader(HttpHeaders.ACCEPT_ENCODING,"gzip,deflate");
		httpBase.setHeader(HttpHeaders.CONNECTION,"Keep-Alive");
		httpBase.setHeader(HttpHeaders.USER_AGENT,"Apache-HttpClient/4.1.1 (java 1.5)");
	}

}
