<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <h1>Unsubscribe From Our Mailing List</h1>
    
    <form id="unsubscribeForm" method="POST" action="/Email/" name="unsubscribeForm">
		<input type="hidden" name="action" value="unregister"/>
	    
	    <p>We are sorry to see you go and hope to hear from you again.</p>
	    <p>Enter your email address below and click Submit to unsubscribe from our mailing list.</p>
	    <div class="email-signup">
        	<h2>Email:</h2> 
	        <input class="field" type="text" id="emailAddress" value=""/>
	        <input id="submitUnsubscribe" name="submitUnsubscribe" class="button" type="button" value="Unsubscribe" onclick="unsubscribeEmail()"/>
	    </div>
    </form>
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>