<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ page import="com.google.appengine.api.images.ImagesService" %>
<%@ page import="com.google.appengine.api.images.ImagesServiceFactory" %>

<%
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
 //   		String url = imagesService.getServingUrl(new BlobKey(key));
 //   <%= blobstoreService.createUploadUrl("/Admin/Upload/Phone") %>
%>
<div id="content">
    <ul class="breadcrumbs">
        <li><a href="/">Home</a></li>
        <li>&raquo;</li>
        <li><a href="/items/${phone.category}">${phone.category}</a></li>
        <li>&raquo;</li>
        <li>${phone.name}</li>
    </ul>
   	
    <!-- If logged in, set up text box and hidden fields for editing. -->
    <c:if test="${sessionScope.authenticated}">
        <form name="modifyPhone" id="modifyPhone" action="/Admin/" method="POST">
            <input name="action" type="hidden" value="modifyPhone"/>
            <input name="phoneId" type="hidden" value="${phone.id}"/>
<%--
            <input name="phoneDirectory" type="hidden" value="${phone.directory}"/>
            <input name="phoneImgCount" type="hidden" value="${phone.imageCount}"/>
--%>
            <input name="phoneCategory" type="hidden" value="${phone.category}"/>
            <label style="display:none;">Name: </label>
            <input style="display:none;" name="phoneName" type="text" value="${phone.name}" onkeyup="phoneDetailChanged();"  maxlength="64"/>
    </c:if>
    <!-- End Login Check -->
    
    <h1>${phone.name}</h1>
    
    <div class="product-area">
        <c:if test="${!empty ErrorMessage}">
            <div class="error">${ErrorMessage}</div>
        </c:if>
        <c:if test="${empty ErrorMessage}">
            <div class="product-image">
                <a rel="shadowbox[${phone.name}];player=img;" href="${phone.firstImageUrl}">
                    <img src="${phone.firstImageUrl}=s260" border="0" alt="${phone.category} - ${phone.name} 1" />
                </a>
            </div>
            <div class="product-info" id="${phone.id}">
                <div class="description">
                    <!-- If logged in, setup textArea for editing.-->
                    <c:choose>
                        <c:when test="${sessionScope.authenticated}">
                            <label style="display:none;">Description: </label>
                            <textarea name="phoneDesc" style="display:none;" onkeyup="phoneDetailChanged();">${phone.description}</textarea>
                            <p>${phone.description}</p>
                            <label style="display:none;">To add a link: '&lt;a href="URL" target="_blank"&gt;LINK NAME&lt;/a&gt;'</label>
                            <label style="display:none;"> <br/>To add a new line: '&lt;br/&gt;'<br/></label>
                        </c:when>
                        <c:otherwise>
                            <p>${phone.description}</p>
                        </c:otherwise>
                    </c:choose>
                    <!-- End Login Check -->
                </div>
                <ul class="additional-images">
                
                	<c:if test="${fn:length(phone.imageUrls) > 1}">
	                    <c:forEach var="i" begin="1" end="${fn:length(phone.imageUrls)-1}">
	                        <li>
	                            <a rel="shadowbox[${phone.name}];player=img;" href="${phone.imageUrls[i]}">
	                                <img src="${phone.imageUrls[i]}=s60-c" border="0" alt="${phone.category} - ${phone.name} ${i}" />
	                            </a>
	                        </li>
	                    </c:forEach>
	                </c:if>
<%--
                    <c:forEach var="i" begin="2" end="${phone.images.size}">
                        <li>
                            <a rel="shadowbox[${phone.name}];" href="/phones/${phone.directory}/${i}.jpg">
                                <img src="/phones/${phone.directory}/${i}-thumb.jpg" border="0" alt="${phone.directory} - ${phone.name} ${i}.jpeg" />
                            </a>
                        </li>
                    </c:forEach>
--%>
                </ul>
            </div>
        </c:if>
        <div class="clear-left"></div>
    </div>
    
    
    <!-- If logged in, present save button. -->
    <c:if test="${sessionScope.authenticated}">
            <br/>
            <input id="modifyPhoneButton" class="disabled button" type="button" value="Save Changes" onclick="savePhone();"/>
            <input id="modifyPhoneToggle" class="button" type="button" value="Show Edit View" onclick="toggleModifyPhoneView(this);"/>
            <!-- 
            <input id="watermarkPhoneButton" class="button" type="button" value="(Re)Watermark Phone" onclick="watermarkPhone();"/>
            -->
            <input id="deletePhoneButton" class="button" type="button" value="Delete Phone" onclick="deletePhone();"/>
            <p id="modifyPhoneResult" style="display:none;"/>
        </form>
    </c:if>
    <!-- End Login Check -->
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>