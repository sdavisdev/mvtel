
/*
    <!-- display each link from the server -->
    <c:forEach var="link" items="${websiteLinks}">
        <p id="linkId:${link.id}">
            <a href="${link.url}" target="_blank"><b>${link.name}</b></a><br/>
            ${link.description}
            
            <!-- Display the link controls when authenticated -->
            <c:if test="${sessionScope.authenticated}">
                <br/>
                <input id="modifyLinkButton:${link.id}" class="disabled button" type="button" value="Save Changes" onclick="saveLink(${link.id});"/>
                <input id="modifyLinkToggle:${link.id}" class="button" type="button" value="Show Edit View" onclick="toggleModifyLinkView(this);"/>
                <input id="deleteLinkButton:${link.id}" class="button" type="button" value="Delete Link" onclick="deleteLink(${link.id});"/>
                <p id="modifyLinkResult:${link.id}" style="display:none;"/>
            </c:if>
        </p>
    </c:forEach>
*/    
function saveLink(linkId)
{
    var linkHref;
    var linkText;
    var linkDesc;
    
    var showNormal = true;
    if(linkId != 0)
    {
        var toggleBtn = document.getElementById("modifyLinkToggle:" + linkId);
        var buttonText = toggleBtn.value;
        showNormal = (buttonText == "Show Normal View");
    }
    
    if(showNormal)
    {
        linkHref = $("#linkId\\:" + linkId + "\\:href").prop("value");
        linkText = $("#linkId\\:" + linkId + "\\:text").prop("value");
        linkDesc = $("#linkId\\:" + linkId + "\\:desc").prop("value");
    }
    else
    {
        linkHref = $("#linkId\\:" + linkId + " a").prop("href");
        linkText = $("#linkId\\:" + linkId + " a").text();
        linkDesc = $("#linkId\\:" + linkId + " span").text();
    }
    
    var postData = {};
    postData["action"] = "saveLink";
    postData["linkTitle"] = linkText;
    postData["linkUrl"] = linkHref;
    postData["linkDescription"] = linkDesc;
    postData["linkId"] = linkId;
    postData["linkOrder"] = 0;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                document.location = document.location;
            }
            else
                alert("Failed to save link: "+ response.message);
        }
    });
}

function toggleModifyLinkView(linkId)
{
    var toggleBtn = document.getElementById("modifyLinkToggle:" + linkId);
    var buttonText = toggleBtn.value;
    
    var showNormal = (buttonText == "Show Normal View");
    if(showNormal)
    {
        var linkHref = $("#linkId\\:" + linkId + "\\:href").prop("value");
        var linkText = $("#linkId\\:" + linkId + "\\:text").prop("value");
        var linkDesc = $("#linkId\\:" + linkId + "\\:desc").prop("value");
        
       $("#linkId\\:" + linkId + " div").after(
            "<a href='" + linkHref + "' target='_blank'><b>" + linkText + "</b></a><br/>"
            + "<span>" + linkDesc + "</span>");
       $("#linkId\\:" + linkId + " div").remove();
       
        // Toggle button text
        toggleBtn.value = "Show Edit View";
    }
    else
    {
        var linkHref = $("#linkId\\:" + linkId + " a").prop("href");
        var linkText = $("#linkId\\:" + linkId + " a").text();
        var linkDesc = $("#linkId\\:" + linkId + " span").text();
        
        $("#linkId\\:" + linkId + " a").after(
            "<div>"
             + "<label for='linkId:" + linkId + ":text'>Title: </label>"
             + "<input type='text' id='linkId:" + linkId + ":text' value='" + linkText + "' onkeyup='linkDetailChanged(" + linkId + ")'/><br/>"
             + "<label for='linkId:" + linkId + ":href'>URL: </label>"
             + "<input type='text' id='linkId:" + linkId + ":href' value='" + linkHref + "' onkeyup='linkDetailChanged(" + linkId + ")'/><br/>"
             + "<label for='linkId:" + linkId + ":desc'>Description: </label>"
             + "<textarea type='text' id='linkId:" + linkId + ":desc' onkeyup='linkDetailChanged(" + linkId + ")'>" + linkDesc + "</textarea><br/>"
          + "</div>");
        
        $("#linkId\\:" + linkId + " a").next().next().remove();
        $("#linkId\\:" + linkId + " a").remove();
        $("#linkId\\:" + linkId + " span").remove();

        // Toggle button text
        toggleBtn.value = "Show Normal View";
    }
}
function deleteLink(linkId)
{
    var toggleBtn = document.getElementById("modifyLinkToggle:" + linkId);
    var buttonText = toggleBtn.value;
    var showNormal = (buttonText == "Show Normal View");
    var linkText;
    if(showNormal)
    {
        linkText = $("#linkId\\:" + linkId + "\\:text").prop("value");
    }
    else
    {
        linkText = $("#linkId\\:" + linkId + " a").text();
    }
    
    if(!confirm("Are you sure you want to delete link: " + linkText + "?"))
        return;
        
    var postData = {};
    postData["action"] = "deleteLink";
    postData["linkId"] = linkId;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                document.location = document.location;
            }
            else
                alert("Failed to delete link: "+ response.message);
        }
    });
}

function linkDetailChanged(linkId)
{
     $("#modifyLinkButton\\:"+linkId).removeClass("disabled");
}