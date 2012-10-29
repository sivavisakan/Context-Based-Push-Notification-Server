package edu.cmu;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
public class Tester extends ServerResource {
	@Get("html")
	  public String represent() {
		String s = "Hello World !!";
		return s; 
	}
}
