package edu.cmu.safecity;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import edu.cmu.CommonUtilities;
import edu.cmu.LocationUtils;
/**
 * 
 * @author Siva
 *
 */
public class DisseminateAlert extends ServerResource {
	final String SENDER_KEY = "AIzaSyCB50hIjBNB_eP8oJet8lRNs7ZnPOM0Uyo";
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Sender sender = new Sender(SENDER_KEY);
	@Post
	public Representation represent(Representation j) {
		try {
			Queue queue = QueueFactory.getDefaultQueue();
			String postData =  getRequest().getEntity().getText();
			JSONObject myjson = new JSONObject(postData);
			String pushMessage = myjson.getString("message");
			JSONObject severity = myjson.getJSONObject(CommonUtilities.SEV);
			String warning = severity.getString(CommonUtilities.WARN);
			String minor = severity.getString(CommonUtilities.MINOR);
			String medium = severity.getString(CommonUtilities.MEDIUM);
			String major = severity.getString(CommonUtilities.MAJOR);
			/**
			 * Here you were trying to call the push API by using Oauth token and GAE withURL function
			 * Ask kristoffer how to do a post request with the oauth token and reach the protected resource
			 */
			
//		    Reference meRefProfile = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect1/profile");
//		    meRefProfile.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, "488f5d7811b8340bb6d00fbd980269fc8525f6283096b4b6f5dcf01c114cf78cee7fc79ea6b9c3d5");
//		    ClientResource meResourceProfile = new ClientResource(getContext(),meRefProfile);
//		    Representation meReprProfile = meResourceProfile.get();
//		    String profileData = meReprProfile.getText();
			String regIdTemp = "";
			String pl = "{regId:\""+regIdTemp+"\",pushMessage:\""+pushMessage+"\"}";
			//queue.add(withUrl("/protect1/push").param().payload(pl).countdownMillis(3000));
			
			JSONArray as = myjson.getJSONArray("area");
			Double srcLat = as.getDouble(0);
			Double srclon = as.getDouble(1);
			Double srcRad = as.getDouble(2)/1000;
			Query s = new Query("Area");
			PreparedQuery userQuery = datastore.prepare(s);
			Iterable<Entity> user = userQuery.asIterable();
			Iterator<Entity> userIterator = user.iterator();
			ArrayList<Key> parentKeys = new ArrayList<Key>();
			while(userIterator.hasNext()){
				Entity element = (Entity)userIterator.next();
				String severityDst = (String)element.getProperty(CommonUtilities.SEV);
				StringTokenizer severityTokens = new StringTokenizer(severityDst.substring(1, (severityDst.length()-1)), ",");
				String warningDst = severityTokens.nextToken().equals("true") ? "true" : "none";
				String minorDst =  severityTokens.nextToken().equals("true") ? "true" : "none";
				String mediumDst =  severityTokens.nextToken().equals("true") ? "true" : "none";
				String majorDst =  severityTokens.nextToken().equals("true") ? "true" : "none"; 
				GeoPt point = (GeoPt) element.getProperty("point");
				float dstLat = point.getLatitude();
				float dstLon = point.getLongitude();
				float dstRad = Float.parseFloat((String)element.getProperty("rad"));
				Boolean flag = LocationUtils.isOverlapping(srcLat, srclon, srcRad, dstLat, dstLon, dstRad);
				if(flag == true){	
					if(warningDst.equals(warning) || minorDst.equals(minor)||majorDst.equals(major) || mediumDst.equals(medium)){
						Object parentObject = element.getParent();
						if(parentKeys.contains(parentObject) == false){
							parentKeys.add(element.getParent());
						}
					}
				}
			  }
			Query currentLocation = new Query("location");
			PreparedQuery locationQuery = datastore.prepare(currentLocation);
			Iterable<Entity> location = locationQuery.asIterable();
			Iterator<Entity> locationIterator = location.iterator();
			while(locationIterator.hasNext()){
				Entity element = (Entity)locationIterator.next();
				GeoPt point = (GeoPt) element.getProperty("point");
				float dstLat = point.getLatitude();
				float dstLon = point.getLongitude();
				float dstRad = 5;
				Boolean flag = LocationUtils.isOverlapping(srcLat, srclon, srcRad, dstLat, dstLon, dstRad);
				if(flag == true){	
						Object parentObject = element.getParent();
						if(parentKeys.contains(parentObject) == false){
							parentKeys.add(element.getParent());
						}
				}
			}			
			for(Key parent : parentKeys){
				Entity alertUser= datastore.get(parent);
				String email = alertUser.getKey().getName();
				long messageIdLong = System.currentTimeMillis();
				String messageId = messageIdLong+"";
				String notificationMessage = "{Id:\""+messageId+"\",message:\""+pushMessage+"\"}";
				Message message = new Message.Builder().addData("msg",notificationMessage).delayWhileIdle(false).build();		
				String[] contextIDs = getRegId(email);
				String phone =  contextIDs[1];
				String regId  = contextIDs[0];
				System.out.println("The regID retrieved is "+regId);
				Result result = sender.send(message, regId, 5);
				//String messageId = result.getMessageId();
				Entity history = new Entity("History",messageId, alertUser.getKey());
			    history.setProperty("message", pushMessage);
			    history.setProperty("phone", phone);
			    history.setProperty("sent", "0");
			    history.setProperty("type", "Push");
			    history.setProperty("timestamp", System.currentTimeMillis()+"");
			    datastore.put(history);
			    //ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);  
			    //System.out.println(stpe.getCorePoolSize()); 
			    //Runnable sms = new SMSThread(messageId, accessToken);
			    String payload = "{Id:\""+messageId+"\",email:\""+email+"\"}";
			    queue.add(withUrl("/protect3/sms").payload(payload).countdownMillis(5000));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Finds the GCM ID && phone number of the device that is used currently
	 * result[0] = GCM ID
	 * result[1] = phone number
	 * @param email
	 * @return
	 */
	public String[] getRegId(String email){
		Key userKey = KeyFactory.createKey("User",email);
		String defaultId = null;
		Query s = new Query("device",userKey);
		PreparedQuery deviceQuery = datastore.prepare(s);
		Iterable<Entity> device = deviceQuery.asIterable();
		Iterator<Entity> deviceIterator = device.iterator();
//		JSONArray jsonArray = new JSONArray();
		String[] result = new String[2];
		String[] defaultResult = new String[2];
		while(deviceIterator.hasNext()){
			Entity deviceElement = (Entity)deviceIterator.next();
			String regId = (String) deviceElement.getProperty("regId");
			String phone = (String) deviceElement.getProperty("phone");
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"));
			cal.setTime(new Date());
			int hours = cal.get(Calendar.HOUR_OF_DAY);
			int minutes = cal.get(Calendar.MINUTE);
			String start =  (String) deviceElement.getProperty("start");
			String end =  (String) deviceElement.getProperty("end");
			StringTokenizer starttime = new StringTokenizer(start,":");
			int starthour = Integer.parseInt(starttime.nextToken());
			int startMin = Integer.parseInt(starttime.nextToken());
			StringTokenizer endTimeTokens = new StringTokenizer(end,":");
			int endHour = Integer.parseInt(endTimeTokens.nextToken());
			int endMin = Integer.parseInt(endTimeTokens.nextToken());
			Time currentTime = new Time(hours, minutes, 0);
			Time startTime = new Time(starthour, startMin, 0);
			Time endTime = new Time(endHour, endMin,0);
			if(currentTime.after(startTime) && currentTime.before(endTime)){
				 result[0] = regId;
				 result[1] = phone;
				return result;
			}
			//defaultId = regId;
			defaultResult[0] = regId;
			defaultResult[1] = phone;
		}
		return defaultResult;
	}
class SMSThread implements Runnable {
	String messageId;
	String accessToken;
	public SMSThread(String messageId, String accessToken) {
		   this.messageId = messageId;
		   this.accessToken = accessToken;
	   }
	public void run() {
	try {
		  Key messageKey = KeyFactory.createKey("History",messageId);
		  Entity history = datastore.get(messageKey);
		  if(history.getProperty("sent").equals("0")){
			  System.out.println("Successss!");
			  Reference meRef = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect2/sms");
			  meRef.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, accessToken);
			  ClientResource meResource = new ClientResource(getContext(),meRef);
			  Representation meRepr = meResource.get();
			  String result = meRepr.getText();
		  }
		  
	} catch (IOException E){
		
	}
	catch (EntityNotFoundException e) {
			e.printStackTrace();
	}
	}
}
}
