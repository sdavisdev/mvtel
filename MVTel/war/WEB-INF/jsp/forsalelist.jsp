<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<div id="content">
    <ul class="breadcrumbs">
        <li><a href="/">Home</a></li>
        <li>&raquo;</li>
        <li>${category_name}</li>
    </ul>
    <h1>${category_name}</h1>
       
    <c:if test="${!empty ErrorMessage}">
        <div class="error">${ErrorMessage}</div>
    </c:if>

    <c:if test="${empty ErrorMessage}">
        <c:forEach var="item" items="${category_items}" begin="${category_items_begin}" end="${category_items_end}" varStatus="stat">
            
            <c:if test="${!stat.first}">
                <h1>&nbsp;</h1>
            </c:if>
            <!-- If logged in, set up text box and hidden fields for editing. -->
            <c:if test="${sessionScope.authenticated}">
                <form name="modifySaleItem:${item.id}" id="modifySaleItem:${item.id}" action="/Admin/" method="POST">
                    <input name="action" type="hidden" value="modifySaleItem"/>
                    <input name="itemId" type="hidden" value="${item.id}"/>
            </c:if>
            <!-- End Login Check -->
    
            <div class="forsale-area" id="${item.id}"> 
                <h2><span id="item:${item.id}:title">${item.title}</span> - <span id="item:${item.id}:price">${item.price}</span></h2>

                <div class="forsale-image">
                    <!-- Sale item image. Show number 1 if there are images, show MVTel Logo if 0 -->
                    <c:choose>
                        <c:when test="${fn:length(item.imageUrls) >= 1}">
                        
			                <a rel="shadowbox[${item.title}];player=img;" href="${item.firstImageUrl}">
			                    <img src="${item.firstImageUrl}=s150" border="0" alt="${item.title} 1" />
			                </a>
                            
                        </c:when>
                        <c:otherwise>
                            <img src="/images/footer-logo.png" border="0" alt="${item.title}" style="margin-left:-19px;"/>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="forsale-info">
                    <div id="item:${item.id}:description">
                        <p>${item.description}</p>
                    </div>
                    <ul class="additional-images">
                    	<c:if test="${fn:length(item.imageUrls) > 1}">
	                    	<c:forEach var="i" begin="1" end="${fn:length(item.imageUrls)-1}">
		                        <li>
		                            <a rel="shadowbox[${item.title}];player=img;" href="${item.imageUrls[i]}">
		                                <img src="${item.imageUrls[i]}=s60-c" border="0" alt="${item.title} ${i}" />
		                            </a>
		                        </li>
		                    </c:forEach>
	                    </c:if>
                    </ul>
                    <a rel="shadowbox;height=400;width=540;" href="/receiveEmail.jsp?subject=Sale%20Item:%20${item.title}%20(${item.price})">
                        <img src="/images/icon-mail.png"/> Message Me
                    </a>
                </div>
                <div class="clear-left"></div>
                
            </div>
    
            <!-- If logged in, present save button. -->
            <c:if test="${sessionScope.authenticated}">
                    <br/>
                    <input id="modifySaleItemButton:${item.id}" class="disabled button" type="button" value="Save Changes" onclick="saveSaleItem(${item.id});"/>
                    <input id="modifySaleItemToggle:${item.id}" class="button" type="button" value="Show Edit View" onclick="toggleModifySaleView(${item.id});"/>
                    <input id="deleteSaleItemButton" class="button" type="button" value="Delete Item" onclick="deleteSaleItem(${item.id});"/>
                    <p id="modifySaleItemResult:${item.id}" style="display:none;"/>
                </form>
            </c:if>
        </c:forEach>
    </c:if>

    <!-- End Login Check -->
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>