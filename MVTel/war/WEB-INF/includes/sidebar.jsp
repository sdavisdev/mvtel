
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  

<div>
<div id="sidebar">
    <div class="sidebar-wrapper">
        <div class="sidebar-container">
            <h3>Items For Sale</h3>
            <ul class="article-list">
            
                <c:choose>
	            	<c:when test="${fn:length(itemsForSale) > 0}">
		                <c:forEach var="item" items="${itemsForSale}" begin="0" end="4">
                    		<li><a href="/items/For%20Sale/#${item.id}">${item.title}</a></li>
                		</c:forEach>
		                <c:if test="${fn:length(itemsForSale) > 5}">
		            		<li><a href="/items/For%20Sale/">View All Items for Sale</a></li>
		           		</c:if>
		          	</c:when>
		            <c:otherwise>
		            	<li>No Items for Sale at this time</li>
		            </c:otherwise>
		        </c:choose>
            </ul>
            <c:forEach begin="${fn:length(itemsForSale)}" end="4">
            	<p>&nbsp;</p>
            </c:forEach>
        </div>
    </div>
</div>
<div id="sidebar">
    <div class="sidebar-wrapper">
        <div class="sidebar-container">
            <h3>Recent Articles</h3>
            <ul class="article-list">
                <c:choose>
	            	<c:when test="${fn:length(articles) > 0}">
		                <c:forEach var="art" items="${articles}" begin="0" end="4">
		                    <li><a href="/items/Articles/${art.id}">${art.name}</a></li>
		                </c:forEach>
		                <c:if test="${fn:length(articles) > 5}">
		            		<li><a href="/items/Articles/">View All Articles</a></li>
		           		</c:if>
		            </c:when>
		            <c:otherwise>
		            	<li>No Articles at this time</li>
		            </c:otherwise>
                </c:choose>
            </ul>
            <c:forEach begin="${fn:length(articles)}" end="4">
            	<p>&nbsp;</p>
            </c:forEach>
            <!-- 
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            -->
        </div>
    </div>
</div>
</div>