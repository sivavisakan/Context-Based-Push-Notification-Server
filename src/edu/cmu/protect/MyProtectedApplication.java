/**
 * 
 */
package edu.cmu.protect;

import java.util.ArrayList;

import org.restlet.Application;
import org.restlet.Restlet;

import org.restlet.ext.oauth.OAuthAuthorizer;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.routing.Router;
import org.restlet.security.Role;

import edu.cmu.CommonUtilities;


/**
 * @author Martin Svensson
 *
 */
public class MyProtectedApplication extends Application {
  
  @Override
  public synchronized Restlet createInboundRoot() {
    Router router = new Router(getContext());
    OAuthAuthorizer auth = new OAuthAuthorizer(
    		CommonUtilities.SERVER_URL_BASE+"/oauth/validate");
    //set the roles (scopes):
//    ArrayList <Role> roles = new ArrayList <Role> ();
//    roles.add(new Role("status", null));
    auth.setAuthorizedRoles(Scopes.toRoles("status"));
    auth.setNext(StatusResource.class);
    //Defines only one route
    router.attach("/status", auth);
    return router;
  }


}
