package com.expedia.testBNS.common;

import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

@Component
public class SearchNotification extends BaseSend{
	
	public SearchNotification() {
		super();
		httpBase = new HttpPost();
	}
	
	private void initHttp() throws Exception{
		seturl();
		
	}
	
	public JSONObject searchNotification(JSONObject json) throws Exception{
		initHttp();
		if(json!=null){
			setBodyByJSONObj(json);
		}else{
			reader = new JSONReader(new FileReader("source/NSearchV1Req_Customizedforresending.json"));
			setBody("default");
		}
		
		setNewHead();
		
		JSONObject jsonO = execute(httpBase);
		
		return jsonO;
		
	}
	
	private void seturl() throws URISyntaxException{
		URI uri = new URIBuilder()
		        .setScheme("http")
		        .setHost(evoroment.toString())
		        .setPath("/bns/v1/notificationlogs?details=customizedforresending&maxrecordcount=3&gzipcompressenabled=true")
		        .build();
		httpBase.setURI(uri);
	}
	
	private void setNewHead(){
		httpBase.setHeader("Client-ID","1");
	}
	


}
