<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Mike's Vintage Telephones</title>
<link rel="stylesheet" type="text/css" href="css/screen.css" />
</head>

<body>
    <div class="wrapper">
        <c:import url="/WEB-INF/includes/header.jsp"/>
        <div id="main">
        	<div id="content">
            	<ul class="breadcrumbs">
                	<li><a href="">Home</a></li>
                    <li>&raquo;</li>
                    <li><a href="">Pay Stations</a></li>
                    <li>&raquo;</li>
                    <li>AE 62</li>
                </ul>
                <h1>AE 62</h1>
                <div class="product-area">
                	<div class="product-image"><img src="images/ae62-medium.jpg" border="0" alt="" /></div>
                    <div class="product-info">
                    	<div class="description"><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras lobortis mattis tempor. Aliquam at aliquet nulla. Sed luctus bibendum magna, at ornare mi adipiscing non. Aenean vel tortor ac lorem tristique dapibus vitae sed dolor. Morbi mollis, ipsum ac eleifend blandit, nunc.</p></div>
                        <ul class="additional-images">
                        	<li><a href=""><img src="images/ae62-thumb-01.jpg" border="0" alt="" /></a></li>
                            <li><a href=""><img src="images/ae62-thumb-02.jpg" border="0" alt="" /></a></li>
                            <li><a href=""><img src="images/ae62-thumb-03.jpg" border="0" alt="" /></a></li>
                        </ul>
                    </div>
                    <div class="clear-left"></div>
                </div>
            </div>
            <div id="sidebar">
                <div class="sidebar-wrapper">
                    <div class="sidebar-container">
                        <h3>Recent Articles</h3>
                        <ul class="article-list">
                        	<li><a href="">Sample Article Title One</a></li>
                            <li><a href="">Sample Article Title Two</a></li>
                            <li><a href="">Sample Article Title Three</a></li>
                        </ul>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                    </div>
                </div>
            </div>
            <div class="clear-left"></div>
        </div>
        <c:import url="/WEB-INF/includes/footer.jsp"/>
    </div>
</body>
</html>
