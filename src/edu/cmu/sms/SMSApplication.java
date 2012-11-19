package edu.cmu.sms;

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
public class SMSApplication extends Application {
	  @Override
	  public synchronized Restlet createInboundRoot() {
	    Router router = new Router(getContext());
	    //Defines only one route
	    router.attach("/sms", SMSResource.class);
	    return router;
	  }
	}