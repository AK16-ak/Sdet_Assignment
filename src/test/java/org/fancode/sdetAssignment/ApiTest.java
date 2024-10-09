package org.fancode.sdetAssignment;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import java.awt.geom.FlatteningPathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.OctetStreamData;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiTest {
	
	@Test
	public void testUserToDosCompletion()  {
		//SoftAssert softAssert= new SoftAssert();
		try {
			RestAssured.baseURI="https://jsonplaceholder.typicode.com/";
			
			
			//Get all user
			
			Response userdataResponse=RestAssured.given().when().get("/users").then().statusCode(200).extract().response();
			
			//List<Integer> userList=userdataResponse.jsonPath()
			
//			System.out.println(userdataResponse.asString());
//			System.out.println(userdataResponse.asPrettyString());
//			
			//parse  the response
			ObjectMapper objectMapper=new ObjectMapper();
			JsonNode userJsonNode=objectMapper.readTree(userdataResponse.asString());
			//System.out.println("Parsed JSON Successfully");
			
			//filter user belong to Fan Code city
			List<Integer> fancodeuserIdIntegers=new ArrayList<Integer>();
			Iterator<JsonNode> ele=userJsonNode.elements();
			
			while (ele.hasNext()) {
				JsonNode userNode=ele.next();
				JsonNode geoNode=userNode.path("address").path("geo");
				//System.out.println("geonode: " + geoNode.toString());
				float lat=Float.parseFloat(geoNode.path("lat").asText());
				float lng=Float.parseFloat(geoNode.path("lng").asText());
				//System.out.println("user id: " + userNode.path("id").intValue() + ", " + "lat : " + lat  + ", lng: " + lng);
				if(lat >= -40 && lat <= 5  && lng >= 5 && lng <= 100) {
					fancodeuserIdIntegers.add(userNode.path("id").intValue());
			}
		}
			//System.out.println(fancodeuserIdIntegers);
			//check to-do-s completion for each user
			for(Integer userid: fancodeuserIdIntegers) {
				Response todosResponse=RestAssured.given().when().get("/users/"+ userid +"/todos").then().statusCode(200).extract().response();
				List<Boolean> todosList=todosResponse.jsonPath().getList("completed");
				long completeCount=todosList.stream().filter(Boolean::booleanValue).count();
				double percentageCompleted=(double) completeCount/todosList.size()*100;
				//System.out.println("user id: " + userid +", " + "completeion percentage: " + percentageCompleted);
				
				//softAssert.assertTrue(percentageCompleted>50,"user " + userid+ "has greater then  50% task complete" + ", " + todosResponse.asPrettyString());
				
				Assert.assertTrue(percentageCompleted>50,"user " + userid+ "has greater then  50% task complete");
			}
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
}
}
