package edu.cmu.protect.profile;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.ext.oauth.OAuthAuthorizer;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.routing.Router;
import edu.cmu.CommonUtilities;
/**
 * @author Siva 
 *
 */
public class ProfileApplication extends Application {
  
  @Override
  public synchronized Restlet createInboundRoot() {
    Router router = new Router(getContext());
    OAuthAuthorizer auth = new OAuthAuthorizer(CommonUtilities.SERVER_URL_BASE+"/oauth/validate");
    auth.setAuthorizedRoles(Scopes.toRoles("profile"));
    auth.setNext(ProfileResource.class);
    //Defines only one route
    router.attach("/profile", auth);
    return router;
  }
}
