<%@include file="header.jsp" %>  
  <br/><br/><br/>
  			  <% Object status = request.getAttribute("status"); %>
              <% Object total =  request.getAttribute("total"); %>
              <% Object name =  request.getAttribute("firstname"); %>
              <% String welcomeMessage =""; %>
  			  <%if(name!=null) { %>
              <%  welcomeMessage = "Welcome, "+name; } %>
              <%= welcomeMessage %>
              <%if(status==null) %>
              <form method="post" action="/sign">
              <table>
              <%-- <tr><td><% status = ""; %></td></tr>
              <tr><td>Message</td><td><input type="text" class="input-large" name="message" placeholder="Message to be Pushed!"/></td></tr>
              <tr><td>Number of registered devices are</td><td>&nbsp <strong><%= total %></strong></td></tr>
              <tr class="success"><td><%= status %></td></tr> --%>
              <tr><td><br/>Do you want to send an alert message ?</td></tr>
              <tr><td><br/><Button class="btn btn-primary" type="submit" name="map">Alert !</Button></td></tr>
              
              </table>
              </form> 
<%@include file="footer.jsp" %>              