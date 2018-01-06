<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <h1>Links</h1>
    <%--
    <p>
        <a href="http://www.atcaonline.com/" target="_blank"><b>Antique Telephone Collectors Association ATCA</b></a><br/>
        The Antique Telephone Collectors Association, or ATCA, is the largest telephone collectors 
        organization in the world. Chartered in 1971 as a non-profit corporation by the state of Kansas, 
        its over 1000 active members are located throughout the United States, Canada, Europe, and Australia.
    </p>
    <p>
        <a href="http://www.telephonecollectors.org/" target="_blank"><b>Telephone Collectors International TCI</b></a><br/>
        Telephone Collectors International is an organization of telephone collectors, hobbyists and 
        historians who are helping to preserve the history of the telecommunications industry through 
        the collection of telephones and telephone related material. Our collections represent all 
        aspects of the industry; from the very first wooden prototypes that started the industry to 
        the technological marvels that made the automatic telephone exchange possible.
    </p>
    --%>
    
    <!-- Create a form when authenticated -->
    <c:if test="${sessionScope.authenticated}">
        <form id="linkForm" method="POST" action="/Admin/" name="linkForm">
            
        <!-- Create New Link Form -->
        <h2>Create New Link</h2>
        <p id="linkId:0">
            <label for="linkId:0:text">Title: </label>
            <input type="text" id="linkId:0:text" value=""/><br/>
            <label for="linkId:0:href">URL: </label>
            <input type="text" id="linkId:0:href" value=""/><br/>
            <label for="linkId:0:desc">Description: </label>
            <textarea type="text" id="linkId:0:desc"></textarea><br/>
            <input id="modifyLinkButton:0" class="button" type="button" value="Create Link" onclick="saveLink(0);"/>
            <h1></h1>
        </p>
                
    </c:if>
    
    <!-- display each link from the server -->
    <c:forEach var="link" items="${websiteLinks}">
        <p id="linkId:${link.id}">
            <a href="${link.url}" target="_blank"><b>${link.name}</b></a><br/>
            
            <span>${link.description}</span>
            
            <!-- Display the link controls when authenticated -->
            <c:if test="${sessionScope.authenticated}">
                <br/><br/>
                <input id="modifyLinkButton:${link.id}" class="disabled button" type="button" value="Save Changes" onclick="saveLink(${link.id});"/>
                <input id="modifyLinkToggle:${link.id}" class="button" type="button" value="Show Edit View" onclick="toggleModifyLinkView(${link.id});"/>
                <input id="deleteLinkButton:${link.id}" class="button" type="button" value="Delete Link" onclick="deleteLink(${link.id});"/>
                <p id="modifyLinkResult:${link.id}" style="display:none;"/>
                <h1></h1>
            </c:if>
        </p>
    </c:forEach>
        
    <!-- Close the form tag if authenticated -->
    <c:if test="${sessionScope.authenticated}">
        </form>
    </c:if>
</div>
<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>