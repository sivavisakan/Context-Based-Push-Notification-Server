package edu.cmu.safecity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import edu.cmu.CommonUtilities;

public class AreaInfo extends ServerResource {
	@Post 
	public Representation getAreaInfo(Representation j) throws JSONException {
		JSONArray areaArray;
		JSONObject areaJSON = null;
		try {
		String postData =  getRequest().getEntity().getText();
		JSONObject myjson = new JSONObject(postData);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String userID = myjson.getString("username");
		String email = userID+"@cmu.edu";
		System.out.println("The ID for Area retrieval "+email);
		Key userKey = KeyFactory.createKey("User",email);
		Query aq  = new Query("Area",userKey);
		PreparedQuery areaQuery = datastore.prepare(aq);
		Iterable<Entity> area = areaQuery.asIterable();
		Iterator<Entity> areaIterator = area.iterator();
		areaArray = new JSONArray();
		while(areaIterator.hasNext()){
			Entity areaElement = (Entity)areaIterator.next();
			String areaName = areaElement.getKey().getName();
	        GeoPt point = (GeoPt) areaElement.getProperty("point");
	        float lat = point.getLatitude();
	        float lon = point.getLongitude();
	        String severity = (String) areaElement.getProperty("severity");
	        float rad = Float.parseFloat((String) areaElement.getProperty("rad"));
	        Map<String, Object>locationMap = new HashMap<String, Object>();
	        locationMap.put("lat", lat);
	        locationMap.put("lon", lon);
	        locationMap.put("rad", rad);
	        JSONObject locationJson = new JSONObject(locationMap);
	        Map<String,Object>AreaMap = new HashMap<String, Object>();
	        AreaMap.put("location", locationJson);
	        AreaMap.put("name", areaName);
	        StringTokenizer severityTokens = new StringTokenizer(severity.substring(1, (severity.length()-1)), ",");
	        boolean[] severityArray = new boolean[4];
	        severityArray[0] = severityTokens.nextToken().equals("true") ;
	        severityArray[1] = severityTokens.nextToken().equals("true") ;
	        severityArray[2] = severityTokens.nextToken().equals("true") ;
	        severityArray[3] = severityTokens.nextToken().equals("true") ;
			 
	        AreaMap.put("severity",severityArray);
			JSONObject areaJson = new JSONObject(AreaMap);
			areaArray.put(areaJson);
		}
		areaJSON = new JSONObject("{area:"+areaArray+"}");
		System.out.println("Area Successfully retrieved and sent");
		
		} catch(JSONException e){
			System.err.println("Error with JSON inside AreaInfo");
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Error retrieving the area");
			return new JsonRepresentation("{error:\"error retrieveing the policy\"}");
		}
		return new JsonRepresentation(areaJSON);
	}
}
