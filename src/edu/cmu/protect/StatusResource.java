/**
 * 
 */
package edu.cmu.protect;

import java.util.Map;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * @author Martin Svensson
 *
 */
public class StatusResource extends ServerResource{
  
      
    @Get public Representation getUserStatus() throws ResourceException{
        String id = getClientInfo().getUser().getIdentifier();
        String newStatus = this.getQuery().getFirstValue("status");
        Map <String, String> statusMap = StatusMap.getMap();
        String oldStatus = statusMap.get(id);
        if(newStatus != null){
            statusMap.put(id, newStatus);
        }
        return new StringRepresentation("old is the status: "+oldStatus+"\tnew status: "+newStatus);
    }
  }


