/**
 * 
 */
package edu.cmu.protect.profile;
import java.util.logging.Logger;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
/**
 * @author Siva Visakan Sooriyan
 *
 */
public class ProfileResource extends ServerResource{
	private static final Logger log = Logger.getLogger(ProfileResource.class.getName());
	public String BOB_DATA  = "{email:'bob@cmu.edu',phone:'4123223232',locaion:'Mountain View'}";
	public String SIVA_DATA = "{email:'siva@cmu.edu',phone:'4129444959',locaion:'Palo Alto'}";
    @Get public Representation getUserStatus() throws ResourceException{
        String id = getClientInfo().getUser().getIdentifier();
        if(id.equals("siva")){
        	return new StringRepresentation(SIVA_DATA);
        }
        if(id.equals("bob")){
        	return new StringRepresentation(BOB_DATA);
        }
        return null;
    }
}

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        Sender sender = new Sender("AIzaSyCB50hIjBNB_eP8oJet8lRNs7ZnPOM0Uyo");
//        Message message = new Message.Builder().addData("msg", "protected resource accessed").delayWhileIdle(false).build();
//        String siva = "APA91bGv4o3J8YTDTyBveJhfF7xuwfQwm9cOMcA2w4cKNUlwldCY0G9jCMh6O9KD0kOYGeweOSsg7F5JLQDqXJPC2MzDaV9hhNkvXfdFUzCw29Z0CdArw2a6doeMtnsvw9uTjITZp2HZM9rjd7Mi6bkOfrrMiUBmvw";
//        String wook = "APA91bE7nyO_4ZE7NEScF6EZCmPs8RXc0U6YPCgbaSKv6r8-vLmf_rC6H6GKoODZG-mUqVuWtpdfCUXWJYV-fLgX7uQyoqfCa8H3MsF6WmtXAX18bxV4lDFGnyqrX2ndaLfnYwCgkknRut6fQ90bQs_aHl5vOw1GIA";
//        String user = newStatus;//this.getQuery().getFirstValue("user");
//        String regId = siva;
//        if(user != null && user.equals("siva"))
//        	regId = siva;
//        if(user != null && user.equals("wook"))
//        	regId = wook;
//        try {
//			Result result = sender.send(message, regId, 5);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        
        
//        Map <String, String> statusMap = StatusMap.getMap();
//        String oldStatus = statusMap.get(id);
//        if(newStatus != null){
//            statusMap.put(id, newStatus);
//        }
//        return new StringRepresentation("old is the status: "+oldStatus+"\tnew status: "+newStatus);
   