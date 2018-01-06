<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%
	UserService userService = UserServiceFactory.getUserService();
	String logoutUrl = userService.createLogoutURL("/Admin/");
%>
    
	<div id="menu">
        <!-- If logged in, display user name and logout button. -->
        <c:if test="${sessionScope.authenticated}">
            <ul id="loginInfoHeader">
                <li>Logged In As:  <b>${authenticated_user}</b></li>
                <li>
                    <!-- <a href="/Admin/?action=logout">Logout</a> -->
                    <a href="<%= logoutUrl %>">Logout</a>
                </li>
            </ul>
        </c:if>
        <!-- End login check -->
        
        <ul>
            <li><a name="#Home"  href="/?Home">Home</a></li>
            <li><a name="#About" href="/?About">About</a></li>
            <!--<li><a name="#Other" href="/?Other">Other</a></li>-->
            <li><a name="#Press" href="/items/Articles">Telephone Articles</a></li>
            <li><a name="#Links" href="/items/Links">Links</a></li>
            <li><a rel="shadowbox;height=400;width=540;" href="/receiveEmail.jsp">Contact</a></li>
        </ul>
        <div class="clear-left"></div>
    </div>
    <div id="header">
        <h2><a href="/">Mike's Vintage Telephones</a></h2>
        <div class="clear-left"></div>
    </div>
    <div id="navigation">
        <ul>
            <li><a href="#">Pay Stations</a>
            	<ul id="navigation-sub">
            		<li><a href="/items/Paystations">1890's - 1970 Paystations</a></li>
            		<li><a href="/items/Single Slot Payphones">1960's - 2000 Payphones</a></li>
            	</ul>
            </li>
            <li><a href="/items/Wall Phones">Wall Phones</a></li>
            <li><a href="/items/Wood Wall Phones">Wood Wall Phones</a></li>
            <li><a href="/items/Candlestick Phones">Candlestick Phones</a></li>
            <li><a href="/items/Cradle Phones">Cradle Phones</a></li>
            <li><a href="/items/Desk Phones">Desk Phones</a></li>
            <li><a href="/items/For Sale">For Sale</a></li>
            
            <!-- If logged in, display Admin link. -->
            <c:if test="${sessionScope.authenticated}">
                <li><a id="adminButton" href="/Admin/">Admin</a></li>
            </c:if>
        </ul>
        <div class="clear-left"></div>
    </div>