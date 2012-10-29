package edu.cmu;
import edu.cmu.oauth.MyOauthServer;
import edu.cmu.protect.MyProtectedApplication;
import edu.cmu.proxyApplication.MyProxyApplication;


import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

public class TestApplication extends Application {  

	@Override
	public Restlet createInboundRoot() {
		
		if(getContext() == null){
			setContext(new Context());
		}
		Router router = new Router(getContext());
		
		router.attach("/oauth", new MyOauthServer());
		router.attach("/proxy", new MyProxyApplication());
		router.attach("/protect", new MyProtectedApplication());
//		Redirector redirector = new Redirector(getContext(),"/samplerestlet/proxy");
//		//	router.attach("/samplerestlet", Tester.class);
//			router.attachDefault(redirector);
		return router;
	   }
	} 

/*
 * Component c = new Component(); 
		c.getServers().add(Protocol.HTTP, 9090); 
		c.getClients().add(Protocol.HTTP);
		c.getClients().add(Protocol.HTTPS);
		c.getClients().add(Protocol.RIAP);
		c.getClients().add(Protocol.CLAP);

		c.getDefaultHost().attach("/oauth", new MyOauthServer());
		c.getDefaultHost().attach("/proxy", new MyProxyApplication());
		c.getDefaultHost().attach("/protect", new MyProtectedApplication());
		try {
			c.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
		*/

