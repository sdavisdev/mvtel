<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div id="content">
    <ul class="breadcrumbs">
        <li><a href="/">Home</a></li>
        <li>&raquo;</li>
        <li>Telephone Articles</li>
    </ul>
    <h1>Articles</h1>

    <!-- Create a form when authenticated -->
    <c:if test="${sessionScope.authenticated}">
        <form id="articleForm" method="POST" action="/Admin/" name="articleForm">
            
        <!-- Create New Article Form -->
        <h2>Create New Article</h2>
        <p id="articleId:0">
            <label for="article:0:text">Title: </label>
            <input type="text" id="article:0:text" value=""/><br/>
            <label for="article:0:date">Publish Date: </label>
            <input type="text" id="article:0:date" value=""/><br/>
            <label for="article:0:order">Sort Order: </label>
            <input type="text" id="article:0:order" value=""/><br/>
            <label for="article:0:content">Content: </label>
            <textarea type="text" id="article:0:content"></textarea><br/>
            <input id="modifyArticleButton:0" class="button" type="button" value="Create Article" onclick="createArticle();"/>
            <p id="modifyArticleResult:0" style="display:none;"/>
        </p>
        <h1></h1>
                
    </c:if>
    
    <!-- display each article from the server -->
    <c:forEach var="article" items="${articles}">
        <p id="articleId:${article.id}">
            <a href="/items/Articles/${article.id}"><b id="articleId:${article.id}:title">${article.name}</b></a><br/>
            <span>Published: <i id="articleId:${article.id}:date">${article.publishDate}</i></span><br/>
            
            <!-- Display the article content when authenticated -->
            <c:choose>
                <c:when test="${sessionScope.authenticated}">
            		<span>Sort Order: <i id="articleId:${article.id}:order">${article.sortOrder}</i></span><br/>
                    <br/>
                    <span id="articleId:${article.id}:content">${article.content}</span>
                </c:when>
                <c:otherwise>
                    <!-- Display the start of the article when not authenticated -->
                    <%--
                    <br/>
                    <span id="articleId:${article.id}:content">${fn:substring(article.content, 0, 200)} ...</span>
                    --%>
                </c:otherwise>
            </c:choose>
            
        </p>
            <!-- Display the article controls when authenticated -->
            <c:if test="${sessionScope.authenticated}">
                <input id="modifyArticleButton:${article.id}" class="disabled button" type="button" value="Save Changes" onclick="saveArticle(${article.id});"/>
                <input id="modifyArticleToggle:${article.id}" class="button" type="button" value="Show Edit View" onclick="toggleModifyArticleView(${article.id});"/>
                <input id="deleteArticleButton:${article.id}" class="button" type="button" value="Delete Article" onclick="deleteArticle(${article.id});"/>
                <p id="modifyArticleResult:${article.id}" style="display:none;"/>
                <h1></h1>
            </c:if>
    </c:forEach>
        
    <!-- Close the form tag if authenticated -->
    <c:if test="${sessionScope.authenticated}">
        </form>
    </c:if>
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>