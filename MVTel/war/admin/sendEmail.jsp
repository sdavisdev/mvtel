<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <h1>Send Email Form</h1>
    <form name="publishEmailForm" 
          id="publishEmailForm"
          method="post"
          action="/Email"
    >
        <input type="hidden" name="action" value="publish"/>
        
        <!-- Subject -->
        <label for="emailSubject">Subject: </label> 
        <input type="text" name="emailSubject" id="emailSubject" tabindex="1" maxlength="128"/><br/>
        
        <!-- Item Description -->
        <label for="emailMessage">Message: </label> 
        <textarea name="emailMessage" id="emailMessage" tabindex="2"></textarea>


        <p style="line-height: 0.75em;">To add a link: '&lt;a href="URL" target="_blank"&gt;LINK NAME&lt;/a&gt;'</p>
        <p style="line-height: 0.75em;">To add a new line: '&lt;br/&gt;'</p>
        
        <div id="messagePanel"></div>
        
        <div>
            <input id="publishEmailButton" class="button" type="button" value="Send Email" onclick="publishEmail();"/>
            <input id="previewEmailButton" class="button" type="button" value="Preview" onclick="previewEmail();"/>
        </div>
    </form>
</div>