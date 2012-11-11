package edu.cmu.oauth;

import org.restlet.security.SecretVerifier;
/**
 * 
 * @author Siva
 *
 */
public class MyVerifier extends SecretVerifier {
    @Override
    public int verify(String identifier, char[] secret) {
        if("bob".equals(identifier) && compare("alice".toCharArray(),
                secret)){
            return RESULT_VALID;
        }
        else if("siva".equals(identifier) && compare("123".toCharArray(),
                secret)){
        	 return RESULT_VALID;
        }
        else
        	return RESULT_INVALID;
    }
}
