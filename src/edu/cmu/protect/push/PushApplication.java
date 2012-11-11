package edu.cmu.protect.push;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.ext.oauth.OAuthAuthorizer;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.routing.Router;
import edu.cmu.CommonUtilities;
/**
 * 
 * @author Siva
 *
 */
public class PushApplication extends Application {
  @Override
  public synchronized Restlet createInboundRoot() {
    Router router = new Router(getContext());
    OAuthAuthorizer auth = new OAuthAuthorizer(CommonUtilities.SERVER_URL_BASE+"/oauth/validate");
    auth.setAuthorizedRoles(Scopes.toRoles("push"));
    auth.setNext(PushResource.class);
    //Defines only one route
    router.attach("/push", auth);
    return router;
  }


}
