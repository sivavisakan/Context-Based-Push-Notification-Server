package edu.cmu.safecity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.cmu.CommonUtilities;

public class UpdatePolicies extends ServerResource{
	@Post 
	public Representation getAreaInfo(Representation j) throws JSONException {
		JSONArray areaArray;
		try {
		String postData =  getRequest().getEntity().getText();
		JSONObject myjson = new JSONObject(postData);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String userID = myjson.getString("username");
		String email = userID+"@cmu.edu";
		Key userKey = KeyFactory.createKey("User",email);
		String areasStr = myjson.getString(CommonUtilities.AREA);
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
		System.out.println("Successfully updated the Policies of "+email);
		return new StringRepresentation("Successfully updated the Policies of "+email);
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Problem updating the policies "+e.getMessage());
			return new StringRepresentation("Problem updating the policies "+e.getMessage());
		}
	}
}
