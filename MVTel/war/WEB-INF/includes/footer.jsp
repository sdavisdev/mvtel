<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="footer">
	<div class="container">
	    <h3><a href="/">Mike's Vintage Telephones</a></h3>
	    <div class="email-signup">
	    	<h4><a href="/registerEmail.jsp" rel="shadowbox;height=200;width=500;">Sign Up for Our Newsletter</a></h4>
	    	<!--
	        <form action="" method="post" name="" onsubmit="javascript:registerEmail(); return false;">
	        	<input type="text" class="field" name="" value="Enter Email Address..." onkeypress="if(this.value == 'Enter Email Address...'){this.value='';} /*submitenter(this,event,'document.form1');*/" onmousedown="if(event.button == 2 && this.value == 'Enter Email Address...'){ this.value = ''; }" onblur="if (this.value == ''){this.value='Enter Email Address...';}" onclick="if(this.value == 'Enter Email Address...'){this.value='';}" />
	            <input type="button" value="Submit" class="button" onclick="registerEmail();"/>
	            <div class="clear-left"></div>
	        </form>
	        -->
	    </div>
	    <div class="website-hits"><c:out value="${session_hit_count}"/> people have viewed this site</div>
	    <div class="footer-links">
	        <ul>
	            <li><a href="/?Home">Home</a> &nbsp;|&nbsp; </li>
	            <li><a href="/?About">About</a> &nbsp;|&nbsp; </li>
	            <!--<li><a href="/?Other">Other</a> &nbsp;|&nbsp; </li>-->
	            <li><a href="/items/Articles">Telephone Articles</a> &nbsp;|&nbsp; </li>
	            <li><a href="/?Links">Links</a> &nbsp;|&nbsp; </li>
	            <li><a rel="shadowbox;height=400;width=540;" href="/receiveEmail.jsp">Contact</a></li>
	        </ul>
	        <p>&copy; 2011 Mike's Vintage Telephones</p>
	    </div>
	    <div class="clear-left"></div>
	</div>

</div>