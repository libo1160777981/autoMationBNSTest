package com.expedia.testBNS;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.expedia.testBNS.common.AddNotification;
import com.expedia.testBNS.common.UpdateNotification;


public class UpdateNitificationTest extends BaseTest{

	@Autowired
	AddNotification addNotification;
	@Autowired
	UpdateNotification updateNotification;
	JdbcTemplate jdbcTemplate;
	
	@Before
	public void start(){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Test
	public void testa() throws Exception {

		// 先add一个notification
		JSONObject jSONObject = addNotification.addNotification(null);
		String notificationID = jSONObject.getString("NotificationID");
		String NotificationDeliveryId = jSONObject.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		System.out.println("notificationID : "+notificationID+" , NotificationDeliveryId : "+NotificationDeliveryId);

		// 将NotificationDeliveryId对应的状态改为1
		changeStatus(1,NotificationDeliveryId);

		
		// 发送请求
		JSONObject result1 = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId),null);
		assertEquals(true, result1.getBooleanValue("Success"));
		
		String query = "select top 1 * from NotificationDeliveryActive dna where dna.NotificationID="+notificationID+" and dna.NotificationDeliveryID="+NotificationDeliveryId;
		List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
		System.out.println(list);
		
		// 将NotificationDeliveryId对应的状态改为1
		changeStatus(1,NotificationDeliveryId);
		
		JSONReader reader3 = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json"));
		JSONObject jSONObject3 = reader3.readObject(JSONObject.class).getJSONObject("toConfirmed");
		JSONObject result2 = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId),jSONObject3);
		assertEquals(true, result1.getBooleanValue("Success"));
		
	}

	// 验证0->4
	@Test
	public void test0_4() throws Exception {
		JSONObject addResult;
		JSONObject toInQueue;
		JSONObject updateResult;
		JSONObject updateJSON = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json")).readObject(JSONObject.class);
		JSONObject addJSON = new JSONReader(new FileReader("source/NotificationAddV1Request.json")).readObject(JSONObject.class);
		toInQueue = updateJSON.getJSONObject("toInQueue");

		// 先add一个notification
		addResult = addNotification.addNotification(null);
		String notificationID = addResult.getString("NotificationID");
		String NotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 测试没有上一条的OriginalID
		// 将要修改的NotificationDeliveryId对应的状态改为0
		changeStatus(0,NotificationDeliveryId);
		updateResult = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId), toInQueue);
		String expected = "Delivery does not exist previous delivery, cannot update UnSent to InQueue.";
		assertEquals(expected, updateResult.getJSONObject("Error").getString("ErrorMessage"));
				
		//再add一个新的notification，该notification的originnalId为上一个的notificationID
		JSONObject existNotificationIDOriginal = addJSON.getJSONObject("existNotificationIDOriginal");
		existNotificationIDOriginal.getJSONObject("NotificationInfo").put("NotificationIDOriginal", notificationID);
		addResult = addNotification.addNotification(existNotificationIDOriginal);

		// 获得NotificationID和NotificationDeliveryId
		String newNotificationID = addResult.getString("NotificationID");
		String newNotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 将要修改的NotificationDeliveryId对应的状态改为0
		changeStatus(0,newNotificationDeliveryId);

		// 返回成功
		// 將相同的NotificationIDOriginal的最后一条的数据改为0/1/4 
		//0
		changeStatus(0,NotificationDeliveryId);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toInQueue);
		assertEquals(true, updateResult.getBooleanValue("Success"));
		//1
		changeStatus(1,NotificationDeliveryId);
		// 将要修改的NotificationDeliveryId对应的状态改为0
		changeStatus(0,newNotificationDeliveryId);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toInQueue);
		assertEquals(true, updateResult.getBooleanValue("Success"));
		//4
		changeStatus(4,NotificationDeliveryId);
		// 将要修改的NotificationDeliveryId对应的状态改为0
		changeStatus(0,newNotificationDeliveryId);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toInQueue);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		// 返回失败
		// 恢复数据
		changeStatus(0,newNotificationDeliveryId);
		// 將相同的NotificationIDOriginal的最后一条的数据改为2/3/5/6 ,
		changeStatus(2,NotificationDeliveryId);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toInQueue);
		expected = "Previous delivery's status is Confirmed cannot update status to InQueue.";
		assertEquals(expected, updateResult.getJSONObject("Error").getString("ErrorMessage"));

	}

	// 验证0->6 Unsent to toTransitToFax
	@Test
	public void test0_6() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JSONObject addResult;
		JSONObject toTransitToFax;
		JSONObject updateResult;
		String query;
		JSONObject updateJSON = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json")).readObject(JSONObject.class);
		JSONObject addJSON = new JSONReader(new FileReader("source/NotificationAddV1Request.json")).readObject(JSONObject.class);
		toTransitToFax = updateJSON.getJSONObject("toTransitToFax");
		
		// 先add一个notification
		addResult = addNotification.addNotification(null);
		String notificationID = addResult.getString("NotificationID");
		String NotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 测试没有上一条的OriginalID
		// 将要修改的NotificationDeliveryId对应的状态改为0
		query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);
		updateResult = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId), toTransitToFax);
		assertEquals("Delivery does not exist previous delivery, cannot update UnSent to TransitToFax.", updateResult.getJSONObject("Error").getString("ErrorMessage"));
		
		//再add一个新的notification，该notification的originnalId为上一个的notificationID
		JSONObject existNotificationIDOriginal = addJSON.getJSONObject("existNotificationIDOriginal");
		existNotificationIDOriginal.getJSONObject("NotificationInfo").put("NotificationIDOriginal", notificationID);
		JSONObject backData = addNotification.addNotification(existNotificationIDOriginal);

		// 获得NotificationID和NotificationDeliveryId
		String newNotificationID = backData.getString("NotificationID");
		String newNotificationDeliveryId = backData.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 将要修改的NotificationDeliveryId对应的状态改为0
		query = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);

		// 返回成功
		// 將相同的NotificationIDOriginal的最后一条的数据改为5/6 ,
		String query2 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,5,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query2);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toTransitToFax);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		// 返回失败
		// 恢复数据
		String query3 = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query3);
		
		// 將相同的NotificationIDOriginal的最后一条的数据改为0/1/2/3/4 ,
		String query4 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query4);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toTransitToFax);
		assertEquals("Previous delivery's status is UnSent cannot update status to TransitToFax.", updateResult.getJSONObject("Error").getString("ErrorMessage"));

	}

	// 验证0->5  Unsent to toFailToFax
	@Test
	public void test0_5() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JSONObject addResult;
		JSONObject toFailToFax;
		JSONObject updateResult;
		JSONObject updateJSON = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json")).readObject(JSONObject.class);
		JSONObject addJSON = new JSONReader(new FileReader("source/NotificationAddV1Request.json")).readObject(JSONObject.class);
		toFailToFax = updateJSON.getJSONObject("toFailToFax");
		
		// 先add一个notification，
		addResult = addNotification.addNotification(null);
		String notificationID = addResult.getString("NotificationID");
		String NotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");
		
		// 将要修改的NotificationDeliveryId对应的状态改为0
		String query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);
		//测试没有上一条的OriginalID
		updateResult = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId), toFailToFax);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		//再add一个新的notification，该notification的originnalId为上一个的notificationID
		JSONObject existNotificationIDOriginal = addJSON.getJSONObject("existNotificationIDOriginal");
		existNotificationIDOriginal.getJSONObject("NotificationInfo").put("NotificationIDOriginal", notificationID);
		addResult = addNotification.addNotification(existNotificationIDOriginal);

		// 获得NotificationID和NotificationDeliveryId
		String newNotificationID = addResult.getString("NotificationID");
		String newNotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 将要修改的NotificationDeliveryId对应的状态改为0
		query = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);

		// 返回成功
		// 將相同的NotificationIDOriginal的最后一条的数据改为2 ,
		String query2 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,2,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query2);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toFailToFax);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		// 返回失败
		// 恢复数据
		 query = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);
		// 將相同的NotificationIDOriginal的最后一条的数据改为0/1/3/4/5/6 ,
		String query4 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query4);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toFailToFax);
		assertEquals("Previous delivery's status is UnSent cannot update status to FailToFax.", updateResult.getJSONObject("Error").getString("ErrorMessage"));

	}
	
	// 验证0->1 Unsent to Pending
	@Test
	public void test0_1() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JSONObject addResult;
		JSONObject toPending;
		JSONObject updateResult;
		JSONObject updateJSON = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json")).readObject(JSONObject.class);
		JSONObject addJSON = new JSONReader(new FileReader("source/NotificationAddV1Request.json")).readObject(JSONObject.class);
		
		// 先add一个notification
		addResult = addNotification.addNotification(null);
		String notificationID = addResult.getString("NotificationID");
		String NotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");
		
		String query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);
		//测试没有上一条的OriginalID
		toPending = updateJSON.getJSONObject("toPending");
		updateResult = updateNotification.updateNotification(Integer.parseInt(notificationID),
				Integer.parseInt(NotificationDeliveryId), toPending);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		//再add一个新的notification，该notification的originnalId为上一个的notificationID
		JSONObject existNotificationIDOriginal = addJSON.getJSONObject("existNotificationIDOriginal");
		existNotificationIDOriginal.getJSONObject("NotificationInfo").put("NotificationIDOriginal", notificationID);
		addResult = addNotification.addNotification(existNotificationIDOriginal);

		// 获得NotificationID和NotificationDeliveryId
		String newNotificationID = addResult.getString("NotificationID");
		String newNotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
				.getString("NotificationDeliveryId");

		// 将要修改的NotificationDeliveryId对应的状态改为0
		query = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);

		// 返回成功
		// 將相同的NotificationIDOriginal的最后一条的数据改为2 ,
		query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,2,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toPending);
		assertEquals(true, updateResult.getBooleanValue("Success"));

		// 返回失败
		// 恢复数据
		String query3 = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query3);
		
		// 將相同的NotificationIDOriginal的最后一条的数据改为0/1/3/4/5/6 ,
		String query4 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,0,'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query4);

		// 发送请求
		updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
				Integer.parseInt(newNotificationDeliveryId), toPending);
		assertEquals("Previous delivery's status is UnSent cannot update status to Pending.", updateResult.getJSONObject("Error").getString("ErrorMessage"));

	}
	// 验证 no NotificationProviderDeliveryId and NotificationProviderErrorMsg
		@Test
		public void testProviderDeliveryIdAndProviderErrorMsg() throws Exception {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			JSONObject addResult;
			JSONObject toPending;
			JSONObject updateResult;
			JSONObject updateJSON = new JSONReader(new FileReader("source/NotificationUpdateV1Request.json")).readObject(JSONObject.class);
			JSONObject addJSON = new JSONReader(new FileReader("source/NotificationAddV1Request.json")).readObject(JSONObject.class);
			
			// 先add一个notification
			addResult = addNotification.addNotification(null);
			String notificationID = addResult.getString("NotificationID");
			String NotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
					.getString("NotificationDeliveryId");
			
			String query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
					+ " ,0,'Unsent','No Error test1','',0,null";
			jdbcTemplate.execute(query);
			//测试没有上一条的OriginalID
			toPending = updateJSON.getJSONObject("toPending");
			updateResult = updateNotification.updateNotification(Integer.parseInt(notificationID),
					Integer.parseInt(NotificationDeliveryId), toPending);
			assertEquals(true, updateResult.getBooleanValue("Success"));

			//再add一个新的notification，该notification的originnalId为上一个的notificationID
			JSONObject existNotificationIDOriginal = addJSON.getJSONObject("existNotificationIDOriginal");
			existNotificationIDOriginal.getJSONObject("NotificationInfo").put("NotificationIDOriginal", notificationID);
			addResult = addNotification.addNotification(existNotificationIDOriginal);

			// 获得NotificationID和NotificationDeliveryId
			String newNotificationID = addResult.getString("NotificationID");
			String newNotificationDeliveryId = addResult.getJSONArray("NotificationDeliveryResultList").getJSONObject(0)
					.getString("NotificationDeliveryId");

			// 将要修改的NotificationDeliveryId对应的状态改为0
			query = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
					+ " ,0,'Unsent','No Error test1','',0,null";
			jdbcTemplate.execute(query);

			// 返回成功
			// 將相同的NotificationIDOriginal的最后一条的数据改为2 ,
			query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
					+ " ,2,'Unsent','No Error test1','',0,null";
			jdbcTemplate.execute(query);

			// 发送请求
			updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
					Integer.parseInt(newNotificationDeliveryId), toPending);
			assertEquals(true, updateResult.getBooleanValue("Success"));

			// 返回失败
			// 恢复数据
			String query3 = "exec NotificationDeliveryActiveSetStatus#14 " + newNotificationDeliveryId
					+ " ,0,'Unsent','No Error test1','',0,null";
			jdbcTemplate.execute(query3);
			
			// 將相同的NotificationIDOriginal的最后一条的数据改为0/1/3/4/5/6 ,
			String query4 = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
					+ " ,0,'Unsent','No Error test1','',0,null";
			jdbcTemplate.execute(query4);

			// 发送请求
			updateResult = updateNotification.updateNotification(Integer.parseInt(newNotificationID),
					Integer.parseInt(newNotificationDeliveryId), toPending);
			assertEquals("Previous delivery's status is UnSent cannot update status to Pending.", updateResult.getJSONObject("Error").getString("ErrorMessage"));

		}
	
	private void changeStatus(int status,String NotificationDeliveryId){
		String query = "exec NotificationDeliveryActiveSetStatus#14 " + NotificationDeliveryId
				+ " ,"+status+",'Unsent','No Error test1','',0,null";
		jdbcTemplate.execute(query);
	}

}
