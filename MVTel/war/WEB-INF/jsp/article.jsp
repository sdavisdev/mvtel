<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="content">
    <ul class="breadcrumbs">
        <li><a href="/">Home</a></li>
        <li>&raquo;</li>
        <li><a href="/items/Articles">Telephone Articles</a></li>
        <li>&raquo;</li>
        <li>${article.name}</li>
    </ul>
    
    <h1>${article.name}</h1>
    
    <c:choose>
        <c:when test="${!empty ErrorMessage}">
            <div class="error">${ErrorMessage}</div>
        </c:when>
        <c:otherwise>
            <p>Published: <i>${article.publishDate}</i></p>
            <p>${article.content}</p>
        </c:otherwise>
    </c:choose>
        
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>