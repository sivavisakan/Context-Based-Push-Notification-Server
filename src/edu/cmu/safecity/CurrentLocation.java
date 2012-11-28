package edu.cmu.safecity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import edu.cmu.CommonUtilities;

public class CurrentLocation extends ServerResource{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@Post 
	public Representation checkUser(Representation j) throws JSONException {
		try{
			String postData =  getRequest().getEntity().getText();
			JSONObject locationObject = new JSONObject(postData);
			
			String userID = locationObject.getString("username");
			String email = userID+"@cmu.edu";
			Key userKey = KeyFactory.createKey("User",email);
			String lat = locationObject.getString(CommonUtilities.LAT);
	        String lon = locationObject.getString(CommonUtilities.LON);
	        GeoPt point = new GeoPt(Float.parseFloat(lat), Float.parseFloat(lon));
	        Entity location = new Entity("location","current",userKey);
	        location.setProperty("point",point);
			datastore.put(location);
		} catch(Exception e){
			e.printStackTrace();
			return new StringRepresentation("Problem updating the location "+e.getMessage());
}
		return new StringRepresentation("Location updated");
}
	@Get
	public Representation getLocation(){
		try {
		Query s = new Query("User");
		PreparedQuery userQuery = datastore.prepare(s);
		Iterable<Entity> user = userQuery.asIterable();
		Iterator<Entity> userIterator = user.iterator();
		JSONArray jsonArray = new JSONArray();
		while(userIterator.hasNext()){
			Entity userElement = (Entity)userIterator.next();
			String email = userElement.getKey().getName();
			Key locationKey = KeyFactory.createKey(userElement.getKey(),"location","current");
			Entity location = datastore.get(locationKey);
			GeoPt point = (GeoPt) location.getProperty("point");
			String lat = point.getLatitude()+"";
			String lon = point.getLongitude()+"";
			Map<String,String>locationMap = new HashMap<String, String>();
			locationMap.put("user", email);
			locationMap.put("lon", lon);
			locationMap.put("lat", lat);
			jsonArray.put(new JSONObject(locationMap));
		}
		return new JsonRepresentation(jsonArray);
		} catch(Exception e){
			e.printStackTrace();
			return new StringRepresentation("Problem retrieving the location "+e.getMessage());
		}
	}
	
	

}
