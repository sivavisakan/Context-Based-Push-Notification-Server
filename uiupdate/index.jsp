<%@include file="header.jsp" %>
    <div id="index-logo"></div>
    <form id="signin-form" method="post" action="/sign">
              <table id="signGroup">
              <div >
              <tr><td>Email</td> <td><input name="email" id="email" class="span2" type="text" placeholder="Email"></td></tr>
              <tr><td>Password</td> <td> <input name="password" id="password"  class="span2" type="password" placeholder="Password"></td></tr>
              <tr id="signinRow"><td><button type="submit" id="signin" class="btn" name="signin">Sign in</button></td><td>&emsp;<a href="/signup.jsp">Sign up!</a><br/></td></tr>
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