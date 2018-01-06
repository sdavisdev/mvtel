<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Message Mike's Vintage Telephones</title>
    <link rel="stylesheet" type="text/css" href="/css/screen.css" />
    <link rel="stylesheet" type="text/css" href="/css/popups.css" />
    
    <script type="text/javascript" src="/scripts/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="/scripts/mvtel.js"></script>
    <script type="text/javascript" src="/scripts/email.js"></script>
    
	<script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-30692529-1']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
	</script>
</head>
<body>
<div id="content" class="receiveEmail">
    <h1>Send Me A Message</h1>
    <form name="receiveEmailForm" 
          id="receiveEmailForm"
          method="post"
          action="/Email"
    >
        <input type="hidden" name="action" value="publish"/>
        <!-- Name -->
        <label for="emailName">Name: </label> 
        <input type="text" name="emailName" id="emailName" tabindex="1" maxlength="128"/><br/>
        
        <!-- Subject -->
        <label for="emailAddress">Email: </label> 
        <input type="text" name="emailAddress" id="emailAddress" tabindex="2" maxlength="128"/><br/>
        
        <!-- Subject -->
        <label for="emailSubject">Subject: </label> 
        <input type="text" name="emailSubject" id="emailSubject" tabindex="3" maxlength="128" value="${param.subject}"/><br/>
        
        <!-- Item Description -->
        <label for="emailMessage">Message: </label> 
        <textarea name="emailMessage" id="emailMessage" tabindex="4"></textarea>
        
        <div id="messagePanel"></div>
        <div>
            <label for="publishEmailButton"></label> 
            <input id="publishEmailButton" class="button" type="button" value="Send Email" onclick="receiveEmail();"/>
        </div>
    </form>
</div>
</body>
</html>