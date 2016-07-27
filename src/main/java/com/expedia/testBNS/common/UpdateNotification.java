package com.expedia.testBNS.common;

import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

@Component
public class UpdateNotification  extends BaseSend{
	
	public UpdateNotification() {
		super();
		httpBase = new HttpPut();
	}


	private void initHttp(Integer NotificationID,Integer NotificationDeliveryId) throws Exception{
		seturl(NotificationID,NotificationDeliveryId);
		
	}
	
	public JSONObject updateNotification(Integer NotificationID,Integer NotificationDeliveryId,JSONObject json) throws Exception{
		initHttp(NotificationID,NotificationDeliveryId);
		if(json!=null){
			setBodyByJSONObj(json);
		}else{
			reader = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json"));
			setBody("default");
		}
		
		setNewHead();
		
		JSONObject jsonO = execute(httpBase);
		return jsonO;
		
	}
	
	private void seturl(int notifications,int notificationdeliveries) throws URISyntaxException{
		URI uri = new URIBuilder()
		        .setScheme("http")
		        .setHost(evoroment.toString())
		        .setPath("/bns/v1/notifications/"+notifications+"/notificationdeliveries/"+notificationdeliveries)
		        .build();
		httpBase.setURI(uri);
	}
	
	private void setNewHead(){
		httpBase.setHeader("Client-ID","EPS");
	}
	
	
}
