<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Sign Up for Our Newsletter</title>
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
<div id="content" class="registerEmail">
    <h1>Sign Up for Our Newsletter</h1>
    <form name="registerEmailForm" 
          id="registerEmailForm"
          method="post"
          action="/Email"
    >
        <input type="hidden" name="action" value="register"/>
        <!-- Name -->
        <label for="emailName">Name: </label> 
        <input type="text" name="emailName" id="emailName" tabindex="1" maxlength="128"/><br/>
        
        <!-- Email -->
        <label for="emailAddress">Email: </label> 
        <input type="text" name="emailAddress" id="emailAddress" tabindex="2" maxlength="128"/><br/>

        <div id="messagePanel"></div>
        <div>
            <label for="regiserEmailButton"></label> 
            <input id="registerEmailButton" class="button" type="button" value="Register Email" onclick="registerEmail();"/>
        </div>
    </form>
</div>
</body>
</html>