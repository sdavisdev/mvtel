<%-- 
    Document   : createphone
    Created on : Jul 27, 2011, 10:05:56 PM
    Author     : Steve
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <applet id="jumpLoaderApplet" name="jumpLoaderApplet"
		code="jmaster.jumploader.app.JumpLoaderApplet.class"
		archive="/admin/applets/jumploader_z.jar"
		width="600"
		height="400" 
		mayscript>
	<param name="uc_uploadUrl" value="/uploadHandler"/>
</applet>
    </body>
</html>
