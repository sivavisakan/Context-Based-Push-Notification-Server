package edu.cmu.oauth;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.oauth.AccessTokenServerResource;
import org.restlet.ext.oauth.AuthPageServerResource;
import org.restlet.ext.oauth.AuthorizationServerResource;
import org.restlet.ext.oauth.ClientStore;
import org.restlet.ext.oauth.ClientStoreFactory;
import org.restlet.ext.oauth.HttpOAuthHelper;
import org.restlet.ext.oauth.ValidationServerResource;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import edu.cmu.CommonUtilities;
/**
 * 
 * @author Siva
 *
 */
public class MyOauthServer extends Application {
  @Override
  public synchronized Restlet createInboundRoot() { 
	  if(getContext() == null){
			setContext(new Context());
		}
      //Engine.setLogLevel(Level.FINE);
      Router root = new Router(getContext());
      //Challenge Authenticator
      System.out.println("Incoming request ");
      ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC, "OAuth Test Server");
      au.setVerifier(new MyVerifier());
      au.setNext(AuthorizationServerResource.class);
      root.attach("/authorize", au);
      root.attach("/access_token", AccessTokenServerResource.class);
      root.attach("/validate",ValidationServerResource.class);
      root.attach(HttpOAuthHelper.getAuthPage(getContext()),AuthPageServerResource.class);
      //Set Template for AuthPage:
      HttpOAuthHelper.setAuthPageTemplate("authorize.html", getContext());
      //Dont ask for approval if previously approved
      HttpOAuthHelper.setAuthSkipApproved(true, getContext());
      //Attach Image Directory for our login.html page
      final Directory imgs = new Directory(getContext(), "clap:///img/");
      root.attach("/img", imgs);
      getContext().getLogger().info("done");
      //Finally create a test client:
      //Object[] paramsC = {cassandraHost,cassandraPort,"OAuth2"};
      //ClientStoreFactory.setClientStoreImpl((Class<? extends ClientStore<?>>) MyClientStore.class);
      ClientStore clientStore = ClientStoreFactory.getInstance();
      clientStore.createClient("1234567890", "secret1", CommonUtilities.SERVER_URL_BASE+"/proxy");
      return root;
  }
}
