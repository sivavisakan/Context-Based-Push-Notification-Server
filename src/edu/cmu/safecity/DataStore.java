package edu.cmu.safecity;
import edu.cmu.CommonUtilities;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DataStore extends ServerResource {
	@Post ("form")
	public Representation acceptRepresentation(Representation input) throws ResourceException, JSONException {
		Form form = new Form(input);
		JSONObject me = new JSONObject( form.getFirstValue("data") );
		return input;
	}
	@Post ("json")
	public Representation acceptRepresentation(JsonRepresentation jr) throws ResourceException {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			JSONObject myjson = jr.getJsonObject();//new JSONObject(JSONData);
			String email = myjson.getString(CommonUtilities.EMAIL);
			
			Key userKey = KeyFactory.createKey("User",email);
			Entity user = datastore.get(userKey);
			
			String phone = myjson.getString(CommonUtilities.PHONE);
			String regId = myjson.getString(CommonUtilities.REG_ID);
			user.setProperty("phone", phone);
			user.setProperty("regId", regId);
			user.setProperty("phone", phone);
			datastore.put(user);
			JSONArray areas = myjson.getJSONArray(CommonUtilities.AREA);
			int size = areas.length();
		    for (int i = 0; i < size; i++) {
		    	JSONObject area = areas.getJSONObject(i);
		        String areaName = area.getString(CommonUtilities.NAME);
		        JSONObject locationObject = area.getJSONObject(CommonUtilities.LOCATION);
		        String lat = locationObject.getString(CommonUtilities.LAT);
		        String lon = locationObject.getString(CommonUtilities.LON);
		        String rad = locationObject.getString(CommonUtilities.RAD);
		        GeoPt point = new GeoPt(Float.parseFloat(lat), Float.parseFloat(lon));
		        JSONObject severity = area.getJSONObject(CommonUtilities.SEV);
		        Entity areaEntity = new Entity("Area",areaName,userKey);
		        areaEntity.setProperty("point",point);
		        areaEntity.setProperty("rad", rad);
		        areaEntity.setProperty("severity", severity.toString());
		        datastore.put(areaEntity);
		    }
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
