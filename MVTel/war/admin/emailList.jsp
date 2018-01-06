<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <h1>Registered Emails</h1>
    
    <!-- display each email from the server -->
    
    <table class="emailListTable">
        <thead>
            <td class="email">Email</td>
            <td class="name">Name</td>
            <td class="controls">Edit Controls</td>
        </thead>
        <c:forEach var="email" items="${emailList}">
            <tr id="emailAddress:${email.address}">
                <td class="email">${email.address}</td>
                <td class="name">${email.name}</td>
                <td class="controls">
                    <!--
                    <input id="modifyEmailAddressButton:${email.address}" class="disabled button" type="button" value="Save" onclick="saveEmailAddress(${email.address});"/>
                    <input id="modifyEmailAddressToggle:${email.address}" class="button" type="button" value="Edit" onclick="toggleModifyEmailAddressView(${email.address});"/>
                    -->
                    <input id="deleteEmailAddressButton:${email.address}" class="button" type="button" value="Delete" onclick="deleteEmailAddress('${email.address}');"/>
                    <p id="modifyEmailAddressResult:${email.address}" style="display:none;"/>
                    
                </td>
            </tr>
            
        </c:forEach>
        
    </table>
    
</div>

<c:import url="/WEB-INF/includes/sidebar.jsp" />
<div class="clear-left"></div>