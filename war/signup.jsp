<%@include file="header.jsp" %>
    <form method="post" action="/sign">
              <br/><br/><br/>
              <table id="signGroup">
              <div >
              <tr><td>Email</td> <td><input name="email" id="email" class="span2" type="text" ></td></tr>
              <tr><td>Password</td> <td> <input name="password" id="password"  class="span2" type="password" ></td></tr>
              <tr id="passwordRow"><td>Confirm Password</td> <td> <input id="confirmPassword" name="confirmPassword" class="span2" type="password"></td></tr>
              <tr><td>First Name</td> <td> <input name="firstname" class="span2" type="text"></td></tr>
              <tr><td>Last Name</td> <td> <input name="lastname" class="span2" type="text"></td></tr>
              <tr  id="phoneRow"><td>Phone Number</td> <td><input id="phone" name="phone" class="span2" type="text" ></td></tr>
              <tr><td><button type="submit" id="submit" class="btn" name="signup">Submit</button></td><td>&emsp;<a href="/index.jsp"> I am already a Member</a><br/></td></tr>
              </div>
              </table>
    </form>
     <div id="error">
	 <%	 String error = (String) request.getAttribute("error");
	 	 if(error != null) { %>
	 <%= error %>
	 <%  } %>
	 </div>              
<%@include file="footer.jsp" %>