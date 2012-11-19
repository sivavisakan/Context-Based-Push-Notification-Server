package edu.cmu.safecity;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.OAuthUser;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.cmu.CommonUtilities;
/**
 * 
 * @author Siva
 *
 */
public class LocalResource extends ServerResource{
  @Get("html")
  public Representation represent() {
	try{
    OAuthUser u = (OAuthUser) getRequest().getClientInfo().getUser();
    String id = getRequest().getClientInfo().getUser().getIdentifier();
    String token = u.getAccessToken();
    
    Reference meRef1 = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect2/sms");
	meRef1.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
	ClientResource meResource1 = new ClientResource(getContext(),meRef1);
	Representation meRepr1 = meResource1.get();
	String res1 = meRepr1.getText();
    
    
    
    
    Reference meRef = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect/push");
    meRef.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
    String status = this.getQuery().getFirstValue("status");
    if(status != null){
        meRef.addQueryParameter("status", status); // first time this will not be executed 
    }
    ClientResource meResource = new ClientResource(getContext(),meRef);
    Representation meRepr = meResource.get();
    
    Reference meRefProfile = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect1/profile");
    meRefProfile.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
    ClientResource meResourceProfile = new ClientResource(getContext(),meRefProfile);
    Representation meReprProfile = meResourceProfile.get();
    String profileData = meReprProfile.getText();
    updateUser(profileData, token);
    String pushID = meRepr.getText();
    //Log.info("App engine logging "+profileData);
    if (meResource.getResponse().getStatus().isSuccess()) {
      String toRet = "<html><head></head><body>";
      try{
          toRet += "<br/><p>The push ID retrieved is "+pushID+"</p>";
          //toRet +="<p>Retrieved from: "+meRef.toUrl().toString()+"</p>";
          toRet += "<br/><p>The profile data retrieved is "+profileData+"</p>";
      }
      catch(Exception e){
    	  toRet += "could not retrieve information";
      }
      	  toRet += "</body></html>";
      meResource.getResponse().release();
      meResource.release();
      
      
      getResponse().redirectTemporary(meRef);
      return null;
      //return new StringRepresentation(toRet, MediaType.TEXT_HTML);
    }
    return null;
	  }catch (Exception e) {
		  e.printStackTrace();
		  return null;
		}
  } 
  
public void updateUser(String profile,String token){
	  try {	
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  	JSONObject myjson = new JSONObject(profile);
		String email = myjson.getString(CommonUtilities.EMAIL);
		Entity user;
		//Check if the email ID is already registered
		try {
			Key userKey = KeyFactory.createKey("User",email);
			user = datastore.get(userKey);
		} catch (EntityNotFoundException e) {
			user = new Entity("User", email);
		}
		user.setProperty("token", token);
	    datastore.put(user);
		} 
	  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}