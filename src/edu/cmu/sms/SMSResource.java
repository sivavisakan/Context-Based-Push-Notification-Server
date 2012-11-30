package edu.cmu.sms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;

import edu.cmu.CommonUtilities;
import edu.cmu.safecity.DataStore;
/**
 * 
 * @author Sungho
 * @author Siva
 *
 */
public class SMSResource extends ServerResource{
	
	private final String ACCOUNT_SID = "ACba7ed014c32e4e255637ea6a45557c6b";
	private final String AUTH_TOKEN = "aeed6fdfdcfdb89ced0d2bbaa50569ad";
	private final String TWILIO_PHONE_NUMBER = "+16502657898";
	private TwilioRestClient client;
	private Account account;
	private SmsFactory smsFactory;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@Post ("json")
    public Representation acceptRepresentation(JsonRepresentation s){
	try {
		JSONObject myjson = s.getJsonObject();
		String messageId = myjson.getString("Id");//getQueryValue("messageId");
		String email = myjson.getString("username")+"@cmu.edu";
		Key userKey = KeyFactory.createKey("User",email);
		Key historyKey = KeyFactory.createKey(userKey,"History",messageId);
		Entity history = datastore.get(historyKey);
		history.setProperty("sent","1");
		System.out.println("Acknowledgement for Push received");
		datastore.put(history);
		return new StringRepresentation("successfully receipt sent");
		}catch(Exception e) {
    		e.printStackTrace();
    		return new StringRepresentation("something went wrong; Reason: "+e.getMessage());
    	}
    }
	@Post
    public Representation represent(Representation j) throws ResourceException{
    	try {
		String postData =  getRequest().getEntity().getText();
    	JSONObject myjson = new JSONObject(postData);
    	String messageId = myjson.getString("Id");
    	String email = myjson.getString("email");
    	Key userKey = KeyFactory.createKey("User",email);
    	Key historyKey = KeyFactory.createKey(userKey,"History",messageId);
		Entity history = datastore.get(historyKey);
		String check = (String) history.getProperty("sent");
		if(check.equals("0")){
		System.out.println("Sending SMS since there is no acknowledgement");
		
		
		
		String to = (String) history.getProperty(CommonUtilities.PHONE); 		// Write The code to check the flag and send the SMS
		String msg = (String)history.getProperty("message");	
		
		
		Entity historySMS = new Entity("History",System.currentTimeMillis(), userKey);
	    history.setProperty("message", msg);
	    history.setProperty("phone", to);
	    history.setProperty("sent", "1");
	    history.setProperty("type","SMS");
	    history.setProperty("timestamp", System.currentTimeMillis()+"");
	    datastore.put(historySMS);
		
		// Get phone number and message to send SMS
		// Create SMS parameters using above info
		Map<String, String> smsParams = new HashMap<String, String>();
		client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    	account = client.getAccount();
		smsFactory = account.getSmsFactory();
		smsParams.put("To", to);
		smsParams.put("From", TWILIO_PHONE_NUMBER);
		smsParams.put("Body", msg);
		// Send SMS
		smsFactory.create(smsParams);
		return new StringRepresentation("sms sent");
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
    	return new StringRepresentation("push notification delivered");
    }
}
    

