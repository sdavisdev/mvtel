<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Mike's Vintage Telephones - Admin</title>
    <link rel="stylesheet" type="text/css" href="/css/admin.css" />
    <link rel="stylesheet" type="text/css" href="/css/screen.css" />
    <!-- 
    <link rel="stylesheet" type="text/css" href="http://adamtrupkin.com/mvtel/css/styles.css" />
    -->
    
    <script type="text/javascript" src="/scripts/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="/scripts/mvtel.js"></script>
    <script type="text/javascript" src="/scripts/upload.js"></script>
    <script type="text/javascript" src="/scripts/email.js"></script>
    
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

			$(document).ready(function() {
				$( '#navigation ul > li' ).hover(
					function(){
						$(this).find('ul').css('visibility', 'visible'); 
					},
					function(){
						$(this).find('ul').css('visibility', 'hidden'); 
					}
				);
			});
	</script>
</head>
    
<body>
    <div class="wrapper">
        <div class="container">
            <c:import url="/WEB-INF/includes/header.jsp"/>
        
            <div id="main">
                <c:if test="${empty pageToLoad}">
                    <c:choose>
                        <%-- Show an admin page when authenticated --%>
                        <%--
                        <c:when test="${authenticated}">
                            <c:choose>
                        --%>
                                <c:when test="${param.UploadPhone ne null}">
                                    <c:set var="pageToLoad" value="/admin/uploadPhone.jsp"/>
                                </c:when>
                                <c:when test="${param.UploadSaleItem ne null}">
                                    <c:set var="pageToLoad" value="/admin/uploadSaleItem.jsp"/>
                                </c:when>
                                <c:when test="${param.SendEmail ne null}">
                                    <c:set var="pageToLoad" value="/admin/sendEmail.jsp"/>
                                </c:when>
                                <c:when test="${param.EmailList ne null}">
                                    <c:set var="pageToLoad" value="/admin/emailList.jsp"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="pageToLoad" value="/admin/admin.jsp"/>
                                </c:otherwise>
                        <%--
                            </c:choose>
                        </c:when>
                        --%>
                        <%-- Show login page when not authenticated --%>
                        <%-- 
                        <c:otherwise>
                            <c:set var="pageToLoad" value="/admin/login.jsp"/>
                        </c:otherwise>
                        --%>
                    </c:choose>
                </c:if>

                <c:import url="${pageToLoad}"/>

                <div class="clear-left"></div>
            </div>
        </div>
        <c:import url="/WEB-INF/includes/footer.jsp"/>
    </div>
</body>
</html>
