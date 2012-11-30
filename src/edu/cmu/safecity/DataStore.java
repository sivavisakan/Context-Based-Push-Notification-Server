package edu.cmu.safecity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.cmu.CommonUtilities;

public class DataStore extends ServerResource {
	@Post 
	public Representation represent(Representation j) {
		try {
			String postData =  getRequest().getEntity().getText();
			JSONObject myjson = new JSONObject(postData);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String email = myjson.getString("username")+"@cmu.edu";
			Key userKey = KeyFactory.createKey("User",email);
			Entity user = datastore.get(userKey);
			String phone = myjson.getString(CommonUtilities.PHONE);
			String regId = myjson.getString(CommonUtilities.REG_ID);
			String deviceId = myjson.getString("deviceId");
			user.setProperty("registered", "true");
			datastore.put(user);
			System.out.println("A new user has registered "+email);
			Entity device = new Entity("device",deviceId,userKey);
			device.setProperty("start", "00:00");
			device.setProperty("end", "23:59");
			device.setProperty("regId", regId);
			device.setProperty("phone", phone);
			datastore.put(device);
			String areasStr = myjson.getString(CommonUtilities.AREA);
			//JSONObject areaJSON = new JSONObject(areasStr);
			JSONArray areas = new JSONArray(areasStr);			
			int size = areas.length();
		    for (int i = 0; i < size ; i++) {
		    	//if(areas.getString(i).equals(null) != true){
		    	JSONObject area = areas.getJSONObject(i);
		        String areaName = area.getString(CommonUtilities.NAME);
		        JSONObject locationObject = area.getJSONObject(CommonUtilities.LOCATION);
		        String lat = locationObject.getString(CommonUtilities.LAT);
		        String lon = locationObject.getString(CommonUtilities.LON);
		        String rad = locationObject.getString(CommonUtilities.RAD);
		        GeoPt point = new GeoPt(Float.parseFloat(lat), Float.parseFloat(lon));
		        String severityStr = area.getString(CommonUtilities.SEV);
		        Entity areaEntity = new Entity("Area",areaName,userKey);
		        areaEntity.setProperty("point",point);
		        areaEntity.setProperty("rad", rad);
		        areaEntity.setProperty("severity", severityStr);
		        datastore.put(areaEntity);
		       }
		}catch (JSONException e){
			System.err.println("Failed to parse JSON");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
