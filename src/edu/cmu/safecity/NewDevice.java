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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
/**
 * Route that handles the registration of multiple devices
 * @author Siva
 *
 */
public class NewDevice extends ServerResource {
		@Post 
		public Representation registerNewDevice(Representation j) throws JSONException {
			try {
				String postData =  getRequest().getEntity().getText();
				JSONObject myjson = new JSONObject(postData);
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				String userID = myjson.getString("username");
				String email = userID+"@cmu.edu";
				Key userKey = KeyFactory.createKey("User",email);
				String phone = myjson.getString("phone");
				String regId = myjson.getString("regId");
				
				JSONArray devices =myjson.getJSONArray("data");
				for(int i = 0 ; i <devices.length();i++){
					JSONObject device = (JSONObject) devices.get(i);
					String deviceId = device.getString("deviceId");
					try {
						Key deviceKey = KeyFactory.createKey(userKey,"device",deviceId);
						Entity deviceEntity = datastore.get(deviceKey);
						deviceEntity.setProperty("start", device.getString("start"));
						deviceEntity.setProperty("end", device.getString("end"));
						deviceEntity.setProperty("regId", deviceEntity.getProperty("regId"));
						deviceEntity.setProperty("phone", deviceEntity.getProperty("phone"));
						datastore.put(deviceEntity);
						System.out.println("Updating the time of old device , Phone: "+deviceEntity.getProperty("phone"));
					} catch (EntityNotFoundException e ){
						Entity newDeviceEntity = new Entity("device",deviceId,userKey);
						newDeviceEntity.setProperty("start", device.getString("start"));
						newDeviceEntity.setProperty("end", device.getString("end"));
						newDeviceEntity.setProperty("regId", regId);
						newDeviceEntity.setProperty("phone", phone);
						datastore.put(newDeviceEntity);
						System.out.println("A new Device registered with new times, phone number: "+phone);
					}
				}
				System.out.println("Number of devices registered "+devices.length());
				return new StringRepresentation("Successfully updated the devices");
			} catch(Exception e){
				e.printStackTrace();
				return new StringRepresentation("Problem updating device "+e.getMessage());
			}
		}
}
