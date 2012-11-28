package edu.cmu.oauth;

import java.util.Collection;

import org.restlet.ext.oauth.Client;
import org.restlet.ext.oauth.ClientStore;
import org.restlet.ext.oauth.internal.TokenGenerator;

public class MyClientStore extends ClientStore {

	protected MyClientStore(TokenGenerator generator) {
		super(generator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Client createClient(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Client createClient(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteClient(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Client findById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection findClientsForUser(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
