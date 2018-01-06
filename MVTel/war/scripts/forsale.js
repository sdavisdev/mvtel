
function saveSaleItem(itemId)
{
    var numImages = $('#modifySaleItem\\:' + itemId + ' input[name$="itemImgCount"]').val();
    var itemTitle;
    var itemPrice;
    var itemDesc;
        
    var toggleBtn = document.getElementById("modifySaleItemToggle:" + itemId);
    var buttonText = toggleBtn.value;
    
    var showNormal = (buttonText == "Show Normal View");
    if(showNormal)
    {
        itemTitle = $("#item\\:" + itemId + "\\:title").prop("value");
        itemPrice = $("#item\\:" + itemId + "\\:price").prop("value");
        itemDesc = $("#item\\:" + itemId + "\\:description").prop("value");
    }
    else
    {
        itemTitle = $("#item\\:" + itemId + "\\:title").text();
        itemPrice = $("#item\\:" + itemId + "\\:price").text();
        itemDesc = $("#item\\:" + itemId + "\\:description p").html();
    }
    
//    if(!(validateSaleItemForm() && validateImages(false)))
//        return;
    
    showSaleItemMessage(itemId, "Saving Sale Item ...", true);
    
    var postData = {};
    postData["action"] = "saveSaleItem";
    postData["saleId"] = itemId;
    postData["saleTitle"] = itemTitle;
    postData["salePrice"] = itemPrice;
    postData["saleDescription"] = itemDesc;
    postData["numImages"] = numImages;
    
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
            {
                alert("Failed to save sale item: "+ response.message);
            }
        }
    });
}

function toggleModifySaleView(itemId)
{
    var toggleBtn = document.getElementById("modifySaleItemToggle:" + itemId);
    var buttonText = toggleBtn.value;
    
    var showNormal = (buttonText == "Show Normal View");
    if(showNormal)
    {
        var itemTitle = $("#item\\:" + itemId + "\\:title").prop("value");
        var itemPrice = $("#item\\:" + itemId + "\\:price").prop("value");
        var itemDesc = $("#item\\:" + itemId + "\\:description").prop("value");
        
        // replace the title and price inputs with the h2 element containing both
        var titleElem = $("#item\\:" + itemId + "\\:title");
        titleElem.parent().after("<h2><span id='item:" + itemId + ":title'>" + itemTitle + "</span> - <span id='item:" + itemId + ":price'>" + itemPrice + "</span></h2>");
        titleElem.parent().remove();
        
        // replace the description text area with a <p> element
        var descElem = $("#item\\:" + itemId + "\\:description");
        descElem.parent().after("<div id='item:" + itemId + ":description'><p>" + itemDesc + "</p>");
        descElem.parent().remove();
        
        // Toggle button text
        toggleBtn.value = "Show Edit View";
    }
    else
    {
        var itemTitle = $("#item\\:" + itemId + "\\:title").text();
        var itemPrice = $("#item\\:" + itemId + "\\:price").text();
        var itemDesc = $("#item\\:" + itemId + "\\:description p").html();
        
        // replace title and price with input fields
        var titleElem = $("#item\\:" + itemId + "\\:title");
        titleElem.parent().after(
            "<div>"
             + "<label for='item:" + itemId + ":title'>Title: </label>"
             + "<input type='text' id='item:" + itemId + ":title' value='" + itemTitle + "' onkeyup='saleItemDetailChanged(" + itemId + ")'/><br/>"
             + "<label for='item:" + itemId + ":price'>Price: </label>"
             + "<input type='text' id='item:" + itemId + ":price' value='" + itemPrice + "' onkeyup='saleItemDetailChanged(" + itemId + ")'/><br/>"
          + "</div>");
        titleElem.parent().remove();
        
        // replace description with a text area
        var descElem = $("#item\\:" + itemId + "\\:description");
        descElem.after(
             "<div>" 
               + "<label for='item:" + itemId + ":description'>Description: </label>"
               + "<textarea type='text' id='item:" + itemId + ":description' onkeyup='saleItemDetailChanged(" + itemId + ")'>" + itemDesc + "</textarea><br/>"
               + "<p>To add a link: '&lt;a href=\"URL\" target=\"_blank\"&gt;LINK NAME&lt;/a&gt;'<br/>"
               + "   To add a new line: '&lt;br/&gt;'<br/></p>"
             + "</div>");
        descElem.remove();
        
        // Toggle button text
        toggleBtn.value = "Show Normal View";
    }
}

function watermarkSaleItem(itemId)
{
    var postData = {};
    postData["action"] = "watermarkSaleItem";
    postData["saleItemId"] = itemId;
    
    showSaleItemMessage(itemId, "Watermarking Sale Item ...", true);
    
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
                alert("Failed to watermark sale item " + itemId + ": "+ response.message);
        }
    });
}

function deleteSaleItem(itemId)
{
    if(!confirm("Are you sure you want to delete this sale item?"))
        return;
    
    showSaleItemMessage(itemId, "Deleting Sale Item ...", true);
    
    var postData = {};
    postData["action"] = "deleteSaleItem";
    postData["saleItemId"] = itemId;
    
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
                alert("Failed to delete sale item: "+ response.message);
        }
    });
}

function saleItemDetailChanged(itemId)
{
     $("#modifySaleItemButton\\:"+itemId).removeClass("disabled");
}

function showSaleItemMessage(itemId, aMessage, show)
{
    $("#modifySaleItemResult\\:" + itemId).html(aMessage).css('display' , (show ? '' : 'none'));
}