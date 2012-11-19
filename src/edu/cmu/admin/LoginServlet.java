package edu.cmu.admin;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import edu.cmu.push.Datastore;
public class LoginServlet extends HttpServlet {
	/**
	 * Handles the Login and Sign up of a User
	 */
	private static final long serialVersionUID = 1L;
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			//Get the intance of the datastore
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key UsersKey = KeyFactory.createKey("CAP", "Users"); //Create a UsersKey -> Similar to Users Table
			//Checks if the form is for signup
			if(req.getParameter("signup")!= null){
				//Retrieving the form data
				String email = req.getParameter("email");
				String phone = req.getParameter("phone");
				String password = req.getParameter("password");
				String firstname = req.getParameter("firstname");
				String lastname = req.getParameter("lastname");

				// Create a user Entiry with email as the identity
				Entity user = new Entity(email, UsersKey); //Create a User entity -> SImilar to a Row
				//Set the properties of the user
				user.setProperty("password", password);
				user.setProperty("phone", phone);
				user.setProperty("firstname", firstname);
				user.setProperty("lastname", lastname);

				datastore.put(user); //Store the details of a User
				req.setAttribute("error","<div class=\"alert alert-success\"> You have successfully signed up! Please login with your credentials! </div>");
				req.getRequestDispatcher("/index.jsp").forward(req, resp);
			}
			
			if(req.getParameter("nextAlert1") != null){
				req.getRequestDispatcher("/alert2.jsp").forward(req, resp);
				return;
			}
			if(req.getParameter("nextAlert2") != null){
				req.getRequestDispatcher("/alert3.jsp").forward(req, resp);
				return;
			}
			if(req.getParameter("nextAlert3") != null){
				req.getRequestDispatcher("/alert4.jsp").forward(req, resp);
				return;
			}
			
			if(req.getParameter("backAlert2") != null){
				req.getRequestDispatcher("/alert.jsp").forward(req, resp);
				return;
			}
			//Checking if the form is for showing the map
			if(req.getParameter("map") != null){
				req.getRequestDispatcher("/map.jsp").forward(req, resp);
			}
			//Checks if the form is for sign-in
			if(req.getParameter("signin") != null){
				String mail = req.getParameter("email");
				String password = req.getParameter("password");
				Query query = new Query(mail, UsersKey);
				List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
				if(greetings.size() != 0){
					Entity matchingEntity = greetings.get(0);
					if( matchingEntity != null ){
						String cPassword = (String) matchingEntity.getProperty("password");  
						String cName = (String) matchingEntity.getProperty("firstname");
						//Get the password from the data base and check	  
						if(cPassword.equals(password)){
							req.setAttribute("firstname",cName);
							int total = Datastore.getTotalDevices();
							req.setAttribute("total",total);
							req.getRequestDispatcher("/profile.jsp").forward(req, resp);
							return;
						}
					}
				}	
				//When there is something wrong with the user entered credentials, send an error message!
				req.setAttribute("error","<div class=\"alert alert-error\"> Either your email or Password is wrong; Please try again </div>");
				req.getRequestDispatcher("/index.jsp").forward(req, resp);
				return;
			}
		}
		catch(ServletException e){
			e.printStackTrace();
		}
	}
}