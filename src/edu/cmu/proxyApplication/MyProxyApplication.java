/**
 * 
 */
package edu.cmu.proxyApplication;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.ext.oauth.OAuthParameters;
import org.restlet.ext.oauth.OAuthProxy;
import org.restlet.ext.oauth.internal.OauthProxyV2;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.routing.Router;
import org.restlet.security.Role;

import edu.cmu.CommonUtilities;

/**
 * @author esvmart
 *
 */
public class MyProxyApplication extends Application {
  
  public static final String GOOGLE_OAUTH_BASE = "https://accounts.google.com/o/oauth2/";
  public static final String FACEBOOK_OAUTH = "https://graph.facebook.com/oauth/";
  
  @Override
  public synchronized Restlet createInboundRoot() {
    Router router = new Router(getContext());
    //Define a proxy and resource that access your "local protected resource";
    //List <Role> roles = new ArrayList <Role> ();
    //roles.add(new Role("status", null));
    OAuthParameters params = new OAuthParameters(
      "1234567890",
      "secret1",
      CommonUtilities.SERVER_URL_BASE+"/oauth/",
      Scopes.toRoles("status")); // similar to give acccess to Images, videos
    OAuthProxy local = new OAuthProxy(params,getContext());
    local.setNext(LocalResource.class);
    router.attach("/local",local);
    
    //Define a proxy and resource that access "a google protected resource";
/*    OAuthParameters googlep = new OAuthParameters(
            "167396255185.apps.googleusercontent.com",
            "ebOe_r7Xd5Ibux9dxyH0r5EK", 
            GOOGLE_OAUTH_BASE,
            Scopes.toRoles(GoogleContacts.API_URI));
    googlep.setAuthorizePath("auth");
    googlep.setAccessTokenPath("token");
    OAuthProxy google = new OAuthProxy(googlep, getContext());
    google.setNext(GoogleContacts.class);
    router.attach("/google", google);
    
    //Define a proxy and resource that access you facebook account. Facebook uses
    //an older version of the OAuth2 draft (2). This will change once the draft
    //is finalized
    //We are only showing this since you might want to connect to facebook
    Conf.I().getFbId(),
    Conf.I().getFbSecret(),
    Scopes.toRoles(Conf.I().getFbScopes()),
    getContext().createChildContext());
    OAuthParameters fbp = new OAuthParameters(
            "203909249620072",
            "1928f0a6ad5898a06ed1e5ace33a4289",
            FACEBOOK_OAUTH,
            Scopes.toRoles(FacebookMe.FB_ROLE)
            );
    OauthProxyV2 fb = new OauthProxyV2(fbp, getContext());
    fb.setNext(FacebookMe.class);
    router.attach("/facebook", fb);*/
    
    return router;
  } 


}
