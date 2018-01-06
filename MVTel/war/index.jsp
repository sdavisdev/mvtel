<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="google-site-verification" content="5GpMWQ_DqgDPYzYSgfJYey9i59_jvcaUSNwQO9o53XI" />
    
    <title>Mike's Vintage Telephones</title>
    
    <link rel="stylesheet" type="text/css" href="/css/screen.css" />
    <!-- 
    <link rel="stylesheet" type="text/css" href="http://adamtrupkin.com/mvtel/css/styles.css" />
    -->
    
    <script type="text/javascript" src="/scripts/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="/scripts/mvtel.js"></script>
    <script type="text/javascript" src="/scripts/email.js"></script>
    <!-- 
    <script type="text/javascript" src="/scripts/google-analytics.js"></script>
    -->
    <c:if test="${sessionScope.authenticated}">
        <script type="text/javascript" src="/scripts/links.js"></script> 
        <script type="text/javascript" src="/scripts/articles.js"></script>
        <script type="text/javascript" src="/scripts/forsale.js"></script>
    </c:if>
    
    <!-- Begin Shadowbox Includes -->
    <link rel="stylesheet" type="text/css" href="/shadowbox/shadowbox.css" />
    <script type="text/javascript" src="/shadowbox/shadowbox.js"></script>
    <script type="text/javascript">
        Shadowbox.init({ counterType: "skip", modal: "true", overlayOpacity: 0.8, overlayColor: "#150B05" });
    </script>
    <!-- End Shadowbox Includes -->
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
	<!-- Drop Down Menu -->
	<script type="text/javascript"> 

		$(document).ready(function() {
			$( '#navigation ul > li' ).hover(
				function(){
					//$(this).children('.sub-menu').slideDown(200);
					$(this).find('ul').css('visibility', 'visible'); 
				},
				function(){
					//$(this).children('.sub-menu').slideUp(200);
					$(this).find('ul').css('visibility', 'hidden'); 
				}
			);
		});
		/*
		$(document).ready(function() { 
			$('#navigation ul > li').bind('mouseover', openSubMenu); 
			$('#navigation ul > li').bind('mouseout', closeSubMenu); 
			
			function openSubMenu() { 
				$(this).find('ul').css('visibility', 'visible'); 
			}; 
			function closeSubMenu() { 
				$(this).find('ul').css('visibility', 'hidden'); 
			};
		}); 
		*/
		 
		/*
		$(document).ready(function() {
			
			$('#navigation ul > li').bind('mouseover', openSubMenu); 
			$('#navigation ul > li').bind('mouseout', closeSubMenu);
			$('#navigation ul > li > ul').bind('focus', openSubMenu); 
			$('#navigation ul > li > ul').bind('blur', closeSubMenu); 
			
			function openSubMenu() { 
				$('#navigation ul > li > ul').css('visibility', 'visible'); 
				$('#navigation ul > li > ul').focus();
			}; 
			function closeSubMenu() { 
				$('#navigation ul > li > ul').css('visibility', 'hidden'); 
			};
		}); 
		*/
	</script>
</head>

<body>
    <div class="wrapper">
        <div class="container">
	        <c:import url="/WEB-INF/includes/header.jsp" />
	        
	        <div id="main">
	            <c:if test="${empty pageToLoad}">
	                <c:choose>
	                    <c:when test="${param.Home ne null}">
	                        <c:set var="pageToLoad" value="/home.jspf"/>
	                    </c:when>
	                    <c:when test="${param.About ne null}">
	                        <c:set var="pageToLoad" value="/about.jsp"/>
	                    </c:when>
	                    <c:when test="${param.Links ne null}">
	                        <c:set var="pageToLoad" value="/links.jsp"/>
	                    </c:when>
	                    <c:when test="${param.Press ne null}">
	                        <c:set var="pageToLoad" value="/articles.jsp"/>
	                    </c:when>
	                    <c:when test="${param.UnregisterEmail ne null}">
	                        <c:set var="pageToLoad" value="/emailUnregister.jsp"/>
	                    </c:when>
	                    <c:otherwise>
	                        <c:set var="pageToLoad" value="/home.jspf"/>
	                    </c:otherwise>
	                </c:choose>
	            </c:if>
	            
	            <c:import url="${pageToLoad}" />
	   			
	            <div class="clear-left"></div>
	        </div>
        </div>
        <c:import url="/WEB-INF/includes/footer.jsp" />
        
        <%-- 
	    <c:if test="${pageToLoad eq '/home.jspf'}">
	    	<div class="webring-wrapper">
				<div class="webring" style="width: 430px;">
					<script language=javascript type="text/javascript" src="http://ss.webring.com/navbar?f=j;y=mvtelonline;u=defurl"></script>
					<!--
					<center>Powered by <a href="http://dir.webring.com/rw" target=_top>WebRing</a>.</center>
					-->
					
					<!--optional-->
					<noscript>
						<center>
							<table bgcolor=gray cellspacing=0 border=2>
							<tr><td><table cellpadding=2 cellspacing=0 border=0>
							<tr><td align=center>
							<font face=arial size=-1>
								This site is a member of WebRing. <br>
								To browse visit 
								<a href="http://ss.webring.com/navbar?f=l;y=mvtelonline;u=defurl">Here</a>.
							</font></td></tr></table></td></tr>
							</table>
						</center>
					</noscript>
				</div>
			</div>
	    </c:if>
	    --%>
    </div>
</body>
</html>
