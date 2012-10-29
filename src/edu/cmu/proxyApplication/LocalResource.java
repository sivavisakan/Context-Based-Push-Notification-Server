/**
 * 
 */
package edu.cmu.proxyApplication;

//import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.OAuthUser;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;

import edu.cmu.CommonUtilities;

/**
 * @author Martin Svensson
 *
 */
public class LocalResource extends ServerResource{
  
  @Get("html")
  public Representation represent() {
    
    OAuthUser u = (OAuthUser) getRequest().getClientInfo().getUser();
    String token = u.getAccessToken();
 
    Reference meRef = new Reference(CommonUtilities.SERVER_URL_BASE+"/protect/status");
    
    meRef.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
    String status = this.getQuery().getFirstValue("status");
    if(status != null){
        meRef.addQueryParameter("status", status); // first time this will not be executed 
    }
    ClientResource meResource = new ClientResource(getContext(),meRef);
    Representation meRepr = meResource.get();
 
    if (meResource.getResponse().getStatus().isSuccess()) {
      String toRet = "<html><head></head><body>";
      try{
          toRet += meRepr.getText();
        toRet +="<p>Retrieved from: "+meRef.toUrl().toString()+"</p>";
      }
      catch(Exception e){
        toRet += "could not retrieve information";
      }
      toRet += "</body></html>";
      meResource.getResponse().release();
      meResource.release();
      return new StringRepresentation(toRet, MediaType.TEXT_HTML);
    }
    return null;
  } 


}
