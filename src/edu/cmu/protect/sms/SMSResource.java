package edu.cmu.protect.sms;

import java.util.HashMap;
import java.util.Map;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
/**
 * 
 * @author Sungho
 *
 */
public class SMSResource extends ServerResource{
	private final String ACCOUNT_SID = "ACba7ed014c32e4e255637ea6a45557c6b";
	private final String AUTH_TOKEN = "aeed6fdfdcfdb89ced0d2bbaa50569ad";
	private final String TWILIO_PHONE_NUMBER = "+16502657898";
	private TwilioRestClient client;
	private Account account;
	private SmsFactory smsFactory;
    @Get 
    public Representation sendSMS() throws ResourceException{
    	client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
		account = client.getAccount();
		smsFactory = account.getSmsFactory();
		// Get phone number and message to send SMS
		String to = "+14129444959";//.getParameter("txtTo");
		String msg = "Hello What are you doing";//req.getParameter("txtMsg");
		// Create SMS parameters using above info
		Map<String, String> smsParams = new HashMap<String, String>();
		smsParams.put("To", to);
		smsParams.put("From", TWILIO_PHONE_NUMBER);
		smsParams.put("Body", msg);
		// Send SMS
		try {
			smsFactory.create(smsParams);
		} catch (TwilioRestException e) {
			e.printStackTrace();
		}
        return new StringRepresentation("success");
    }
}
