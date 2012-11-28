<%@include file="header.jsp" %> 
  			  <% Object status = request.getAttribute("status"); %>
              <% Object total =  request.getAttribute("total"); %>
              <% Object name =  request.getAttribute("firstname"); %>
              <% String welcomeMessage =""; %>
  			  <%if(name!=null) { %>
              <%  welcomeMessage = "Welcome, "+name; } %>
              
              <%if(status==null) %>
              <form class="my-form" id="profile-form" method="post" action="/sign">
              <h1><%= welcomeMessage %>!</h1>
              <h1>Do you want to send an alert message ?</h1>
              <Button id="profile-btn" class="btn" type="submit" name="map">Alert !</Button>
              
              <!--<table>
              <%-- <tr><td><% status = ""; %></td></tr>
              <tr><td>Message</td><td><input type="text" class="input-large" name="message" placeholder="Message to be Pushed!"/></td></tr>
              <tr><td>Number of registered devices are</td><td>&nbsp <strong><%= total %></strong></td></tr>
              <tr class="success"><td><%= status %></td></tr> --%>
              
               <tr><td><br/>Do you want to send an alert message ?</td></tr>
              <tr><td><br/><Button class="btn btn-primary" type="submit" name="map">Alert !</Button></td></tr>
              
              </table> -->
              </form> 
<%@include file="footer.jsp" %>              