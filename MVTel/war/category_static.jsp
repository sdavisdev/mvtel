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
                	<li><a href="/">Home</a></li>
                    <li>&raquo;</li>
                    <li>Pay Stations</li>
                </ul>
                <h1>Pay Stations</h1>
                <ul class="product-list">
                	<li>
                    	<div class="product-list-image"><a href=""><img src="images/phone-01.jpg" border="0" alt="" /></a></div>
                        <h3><a href="">AE 29</a></h3>
                    </li>
                    <li>
                    	<div class="product-list-image"><a href=""><img src="images/phone-02.jpg" border="0" alt="" /></a></div>
                        <h3><a href="">AE 62</a></h3>
                    </li>
                    <li>
                    	<div class="product-list-image"><a href=""><img src="images/phone-03.jpg" border="0" alt="" /></a></div>
                        <h3><a href="">GRAY 10A Shield</a></h3>
                    </li>
                    <li>
                    	<div class="product-list-image"><a href=""><img src="images/phone-04.jpg" border="0" alt="" /></a></div>
                        <h3><a href="">AE LPB-82-55 Chrome, Special Chute</a></h3>
                    </li>
                    <div class="clear-left"></div>
                </ul>
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
