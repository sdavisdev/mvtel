
function validateArticle(aTitle, aDate, aContent)
{
    var errorMessage = "";
    if(aTitle == "")
    {
        errorMessage += "\nTitle";
    }
    if(aDate == "")
    {
        errorMessage += "\nPublish Date";
    }
    if(aContent == "")
    {
        errorMessage += "\nContent";
    }
    
    if(errorMessage != "")
    {
        alert("The following field(s) need to be filled in:" + errorMessage);
        return false;
    }
    
    return true;
}

function sendSaveArticle(articleId, title, pubDate, content, sortOrder)
{
    if(!validateArticle(title, pubDate, content))
        return;
    
    showArticleMessage(articleId, "Saving Article ...", true);  
    
    var postData = {};
    postData["action"] = "saveArticle";
    postData["articleId"] = articleId;
    postData["articleTitle"] = title;
    postData["articleDate"] = pubDate;
    postData["articleContent"] = content;
    postData["sortOrder"] = sortOrder;
    
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
                alert("Failed to create article: "+ response.message);
        }
    });
}

function createArticle()
{
    var articleId = -1;
    var title = $("#article\\:0\\:text").prop("value");
    var pubDate = $("#article\\:0\\:date").prop("value");
    var content = $("#article\\:0\\:content").prop("value");
    var sortOrder = $("#article\\:0\\:order").prop("value");
    
    sendSaveArticle(articleId, title, pubDate, content, sortOrder);
}

function saveArticle(articleId)
{
    var sortOrder = 0;
    var articleTitle;
    var articleDate;
    var articleContent;
        
    var toggleBtn = document.getElementById("modifyArticleToggle:" + articleId);
    var buttonText = toggleBtn.value;
    
    var showNormal = (buttonText == "Show Normal View");
    if(showNormal)
    {
        articleTitle = $("#articleId\\:" + articleId + "\\:title").prop("value");
        articleDate = $("#articleId\\:" + articleId + "\\:date").prop("value");
        articleContent = $("#articleId\\:" + articleId + "\\:content").prop("value");
        sortOrder = $("#articleId\\:" + articleId + "\\:order").prop("value");

    }
    else
    {
        articleTitle = $("#articleId\\:" + articleId + "\\:title").text();
        articleDate = $("#articleId\\:" + articleId + "\\:date").text();
        articleContent = $("#articleId\\:" + articleId + "\\:content").html();
        sortOrder = $("#articleId\\:" + articleId + "\\:order").html();
    }
    
    sendSaveArticle(articleId, articleTitle, articleDate, articleContent, sortOrder)
}

function toggleModifyArticleView(articleId)
{
    var toggleBtn = document.getElementById("modifyArticleToggle:" + articleId);
    var buttonText = toggleBtn.value;
    
    var showNormal = (buttonText == "Show Normal View");
    if(showNormal)
    {
        var articleTitle = $("#articleId\\:" + articleId + "\\:title").prop("value");
        var articleDate = $("#articleId\\:" + articleId + "\\:date").prop("value");
        var sortOrder = $("#articleId\\:" + articleId + "\\:order").prop("value");
        var articleContent = $("#articleId\\:" + articleId + "\\:content").prop("value");
        
        $("#articleId\\:" + articleId).html(
            "<a href='/items/Articles/" + articleId + "'>"
          +    "<b id='articleId:" + articleId + ":title'>" + articleTitle + "</b>" 
          + "</a>"
          + "<br/>"
          + "<span>Published: " 
          +    "<i id='articleId:" + articleId + ":date'>" + articleDate + "</i>" 
          + "</span>" 
          + "<br/>"
          + "<span>Sort Order: " 
          +    "<i id='articleId:" + articleId + ":order'>" + sortOrder + "</i>" 
          + "</span>" 
          + "<br/><br/>"
          + "<span id='articleId:" + articleId + ":content'>" + articleContent + "</span>");
        
       
        // Toggle button text
        toggleBtn.value = "Show Edit View";
    }
    else
    {
        var articleTitle = $("#articleId\\:" + articleId + "\\:title").text();
        var articleDate = $("#articleId\\:" + articleId + "\\:date").text();
        var sortOrder = $("#articleId\\:" + articleId + "\\:order").text();
        var articleContent = $("#articleId\\:" + articleId + "\\:content").html();
        
        $("#articleId\\:" + articleId).html(
            "<div>"
             + "<label for='articleId:" + articleId + ":title'>Title: </label>"
             + "<input type='text' id='articleId:" + articleId + ":title' value='" + articleTitle + "' onkeyup='articleDetailChanged(" + articleId + ")'/><br/>"
             + "<label for='articleId:" + articleId + ":date'>Publish Date: </label>"
             + "<input type='text' id='articleId:" + articleId + ":date' value='" + articleDate + "' onkeyup='articleDetailChanged(" + articleId + ")'/><br/>"
             + "<label for='articleId:" + articleId + ":order'>Sort Order: </label>"
             + "<input type='text' id='articleId:" + articleId + ":order' value='" + sortOrder + "' onkeyup='articleDetailChanged(" + articleId + ")'/><br/>"
             + "<label for='articleId:" + articleId + ":content'>Content: </label>"
             + "<textarea type='text' id='articleId:" + articleId + ":content' onkeyup='articleDetailChanged(" + articleId + ")'>" + articleContent + "</textarea><br/>"
          + "</div>");

        // Toggle button text
        toggleBtn.value = "Show Normal View";
    }
}
function deleteArticle(articleId)
{
    var toggleBtn = document.getElementById("modifyArticleToggle:" + articleId);
    var buttonText = toggleBtn.value;
    var showNormal = (buttonText == "Show Normal View");
    var articleText;
    if(showNormal)
    {
        articleText = $("#article\\:" + articleId + "\\:text").prop("value");
    }
    else
    {
        articleText = $("#articleId\\:" + articleId + " a b").text();
    }
    
    if(!confirm("Are you sure you want to delete article: " + articleText + "?"))
        return;
        
    showArticleMessage(articleId, "Deleting Article ...", true);  
      
    var postData = {};
    postData["action"] = "deleteArticle";
    postData["articleId"] = articleId;
    
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
                alert("Failed to delete article: "+ response.message);
        }
    });
}

function articleDetailChanged(articleId)
{
     $("#modifyArticleButton\\:"+articleId).removeClass("disabled");
}

function showArticleMessage(articleId, aMessage, show)
{
    $("#modifyArticleResult\\:" + articleId).html(aMessage).css('display' , (show ? '' : 'none'));
}
    
    