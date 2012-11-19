package edu.cmu.protect.push;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.cmu.CommonUtilities;
/**
 * 
 * @author Siva
 * If the Logged In user has the privilege it will return the Push notification REG_ID, 
 * so that the application can send push notification to the application
 *	
 */
public class PushResource extends ServerResource{
	private static final Logger log = Logger.getLogger(PushResource.class.getName());
	final String SENDER_KEY = "AIzaSyCB50hIjBNB_eP8oJet8lRNs7ZnPOM0Uyo";
	Sender sender = new Sender(SENDER_KEY);
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	String SIVA_DATA = "APA91bGv4o3J8YTDTyBveJhfF7xuwfQwm9cOMcA2w4cKNUlwldCY0G9jCMh6O9KD0kOYGeweOSsg7F5JLQDqXJPC2MzDaV9hhNkvXfdFUzCw29Z0CdArw2a6doeMtnsvw9uTjITZp2HZM9rjd7Mi6bkOfrrMiUBmvw";
	String WOOK_DATA = "APA91bE7nyO_4ZE7NEScF6EZCmPs8RXc0U6YPCgbaSKv6r8-vLmf_rC6H6GKoODZG-mUqVuWtpdfCUXWJYV-fLgX7uQyoqfCa8H3MsF6WmtXAX18bxV4lDFGnyqrX2ndaLfnYwCgkknRut6fQ90bQs_aHl5vOw1GIA";
    @Get public Representation getUserStatus() throws ResourceException{
        String id = getClientInfo().getUser().getIdentifier();
        if(id.equals("siva")){
        	return new StringRepresentation(SIVA_DATA);
        }
        if(id.equals("bob")){
        	return new StringRepresentation(WOOK_DATA);
        }
        if(id.equals("sung")){
        	return new StringRepresentation(WOOK_DATA);
        }
        return null;
  }
    @Post ("json")
    public Representation acceptRepresentation(JsonRepresentation json){
	try {
		JSONObject myjson = json.getJsonObject();
		String regId = myjson.getString(CommonUtilities.REG_ID);//getQueryValue("messageId");
		String message = myjson.getString(CommonUtilities.MESSAGE);
		Message notificationMessage = new Message.Builder().addData("msg",message).delayWhileIdle(false).build();		
		Result result = sender.send(notificationMessage, regId, 5);
		return new StringRepresentation("successfully notification sent");
		}catch(Exception e) {
    		e.printStackTrace();
    		return new StringRepresentation("something went wrong; Reason: "+e.getMessage());
    	}
    }
}
