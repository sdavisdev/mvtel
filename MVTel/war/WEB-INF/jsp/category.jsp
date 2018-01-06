<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
        <c:import url="/WEB-INF/jsp/pageNumbers.jsp?hide_numbers=true"/>
        <ul class="product-list">

            <c:forEach var="phone" items="${category_items}" begin="${category_items_begin}" end="${category_items_end}">
                <li> 
                    <div class="product-list-image">
                        <a href="/items/${phone.category}/${phone.name}">
                        	<img src="${phone.firstImageUrl}=s150" border="0" alt="${category_name} - ${phone.name}" title="${category_name} - ${phone.name}"/>
                        </a>
                    </div>
                    <h3><a href="/items/${phone.category}/${phone.name}">${phone.name}</a></h3>
                </li>
            </c:forEach>
            <div class="clear-left"></div>
            <c:import url="/WEB-INF/jsp/pageNumbers.jsp?hide_words=true"/>
            
        </ul>
    </c:if>
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>
