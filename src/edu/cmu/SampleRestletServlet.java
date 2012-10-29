package edu.cmu;
import org.restlet.*;
import org.restlet.routing.Router;

public class SampleRestletServlet extends Application {  

	@Override
	public Restlet createInboundRoot() {
			Router s = new Router(getContext());
			s.attach("/samplerestlet",Tester.class);
			return s;
	   }
	} 