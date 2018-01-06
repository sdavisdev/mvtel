<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>


<div id="content">
    <h1>Telephone Creation Form</h1>
    <form name="imageUploadForm" 
          id="imageUploadForm"
          method="post"
          enctype="multipart/form-data" 
          action="uploadHandler.jsp"
    >
        <!-- Phone Name -->
        <label for="phoneTitle">Name: </label> 
        <input type="text" name="phoneTitle" id="phoneTitle" tabindex="1" maxlength="64" onblur="checkPhoneExistsNoArg();"/>

        <!-- Phone Category -->
        <label for="phoneCategory">Category: </label> 
        <select name="phoneCategory" id="phoneCategory" tabindex="2" onblur="checkPhoneExistsNoArg();">
            <option value="" selected="true">--SELECT CATEGORY--</option>
            <option value="Paystations" >Paystations</option>
            <option value="Wall Phones">Wall Phones</option>
            <option value="Wood Wall Phones">Wood Wall Phones</option>
            <option value="Candlestick Phones">Candlestick Phones</option>
            <option value="Cradle Phones">Cradle Phones</option>
            <option value="Desk Phones">Desk Phones</option>
        </select>

        <!-- Phone Description -->
        <label for="phoneDescription">Description: </label> 
        <textarea name="phoneDescription" id="phoneDescription" tabindex="3"></textarea>

        <p style="line-height: 0.75em;">To add a link: '&lt;a href="URL" target="_blank"&gt;LINK NAME&lt;/a&gt;'</p>
        <p style="line-height: 0.75em;">To add a new line: '&lt;br/&gt;'</p>

        <applet id="jumpLoaderApplet" name="jumpLoaderApplet"
                code="jmaster.jumploader.app.JumpLoaderApplet.class"
                archive="/admin/applets/jumploader_z.jar"
                width="516"
                height="300" 
                mayscript>
            <!--<param name="uc_uploadUrl" value="/admin/jump/uploadHandler.jsp"/>-->
            <!--
            <param name="uc_uploadUrl" value="<%= blobstoreService.createUploadUrl("/Admin/Upload/Phone") %>"/>
            -->
            <param name="uc_uploadUrl" value="/Admin/Upload/Phone?direct"/>
            <param name="ac_fireAppletInitialized" value="true"/>
            <param name="ac_fireUploaderStatusChanged" value="true"/>
        </applet>
        
        <div id="messagePanel"></div>
        
        <div>
            <input id="createPhoneButton" class="button" type="button" value="Create Phone" onclick="startPhoneUpload();"/>
            <input class="button" type="reset" value="Clear" onclick="clearPhoneForm();"/>
        </div>
    </form>
</div>
