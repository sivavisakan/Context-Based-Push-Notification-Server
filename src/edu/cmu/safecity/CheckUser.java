package edu.cmu.safecity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import edu.cmu.CommonUtilities;

public class CheckUser extends ServerResource {
	@Post 
	public Representation checkUser(Representation j) throws JSONException {
		JSONObject noUser = new JSONObject("{user:\"null\",device:\"false\"}");
		try {
			CommonUtilities.notificationType.put("0", "SMS");
			CommonUtilities.notificationType.put("1", "Push");
			String postData =  getRequest().getEntity().getText();
			JSONObject myjson = new JSONObject(postData);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String userID = myjson.getString("username");
			String deviceId = myjson.getString("deviceId");
			String email = userID+"@cmu.edu";
			Key userKey = KeyFactory.createKey("User",email);
			Entity user = datastore.get(userKey);
			boolean registered = "true".equals((String) user.getProperty("registered"));
			if(registered){
				System.out.println("User is a registered ");
				Query dq  = new Query("device",userKey);
				PreparedQuery deviceQuery = datastore.prepare(dq);
				Iterable<Entity> device = deviceQuery.asIterable();
				Iterator<Entity> deviceIterator = device.iterator();
				boolean devicePresent = false;
				JSONArray deviceArray = new JSONArray();
				while(deviceIterator.hasNext()){
					Entity deviceElement = (Entity)deviceIterator.next();
					String storedDeviceId = deviceElement.getKey().getName();
					if(storedDeviceId.equals(deviceId)){
						devicePresent = true;
					}
					Map<String, String>deviceMap = new HashMap<String,String>();
					deviceMap.put("deviceId", storedDeviceId);
					deviceMap.put("phone", (String)deviceElement.getProperty("phone"));
					deviceMap.put("regId", (String)deviceElement.getProperty("regId"));
					deviceMap.put("start", (String) deviceElement.getProperty("start"));
					deviceMap.put("end", (String) deviceElement.getProperty("end"));
					JSONObject deviceJSON = new JSONObject(deviceMap);
					deviceArray.put(deviceJSON);
				}
				Query s = new Query("History",userKey);
				PreparedQuery historyQuery = datastore.prepare(s);
				Iterable<Entity> history = historyQuery.asIterable();
				Iterator<Entity> historyIterator = history.iterator();
				JSONArray jsonArray = new JSONArray();
				while(historyIterator.hasNext()){
					Entity historyElement = (Entity)historyIterator.next();
					String message = (String) historyElement.getProperty("message");
					String type = (String) historyElement.getProperty("type");
					String delivered = (String)historyElement.getProperty("sent");
					String timestamp = (String) historyElement.getProperty(CommonUtilities.TIMESTAMP);
					Map<String,String>historyMap = new HashMap<String, String>();
					historyMap.put("message", message);
					historyMap.put("type", type);
					historyMap.put("timestamp", timestamp);
					//Send the history only if it is an SMS or a delivered Push Notification
					if("SMS".equals(type) || "Push".equals(type) && "1".equals(delivered)){
						jsonArray.put(new JSONObject(historyMap));
					}
				}
				if(devicePresent)
					System.out.println("Device is a registered device");
				else {
					System.out.println("It is a new device");
					deviceArray.put(new JSONObject("{deviceId:"+deviceId+",start:\"00:00\",end:\"23:59\"}"));
				}
				JSONObject userPresent = new JSONObject("{user:"+email+",history:"+jsonArray+",device:"+devicePresent+",data:"+deviceArray+"}");
				System.out.println("User already present , send the history of push notification");
				return new JsonRepresentation(userPresent);
			}
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("New user registration");
			return new JsonRepresentation(noUser);
		}
		System.out.println("New user registration");
		return new JsonRepresentation(noUser);
	}
}
