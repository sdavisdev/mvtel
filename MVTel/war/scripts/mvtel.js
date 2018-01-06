//$(window).bind( 'hashchange', function(e) {
//    alert(window.location);
//})
//$(window).trigger('hashchange');

function loadContent(pageName)
{
    if("About" == pageName)
        pUrl = "/about.jsp";
    else if("Other" == pageName)
        pUrl = "/other.jspf";
    else if("Press" == pageName)
        pUrl = "/press.jspf";
    else if("Links" == pageName)
        pUrl = "/links.jsp";
    else
        pUrl = "/home.jspf"
        
    $.ajax({
        url: pUrl,
        type: "GET",
        data: {
            action: "getPage",
            pageName: pageName
        },
        context: document.main,
        success: function(response){
            $('#main').html(response);
            window.location.hash = '#' + pageName;
//            window.open('#' + pageName, '_self');
//            $(window).trigger('hashchange');

        }
    });
}

function loadCategory(categoryName)
{
    pUrl = "/items/phones/" + categoryName;
    
    $.ajax({
        url: pUrl,
        type: "GET",
        data: {
            ajax: "true"
        },
        context: document.main,
        success: function(response){
            $('#main').html($(response));
            window.location.hash = "hiya";
        }
    });
    
    
   // $('#main').load(pUrl, {'ajax': true});
}

function savePhone()
{
    var postData = {};
    $("#modifyPhone input:hidden, #modifyPhone input:text, #modifyPhone textArea").each(function() {
        postData[this.name] = this.value;
    });
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                $("#modifyPhoneResult").removeClass('error');
                
                // 'disable' the save button
                if(!$("#modifyPhoneButton").hasClass("disabled"))
                    $("#modifyPhoneButton").addClass("disabled");
            }
            else
                $("#modifyPhoneResult").addClass('error');

            $("#modifyPhoneResult").html(response.message);
            $("#modifyPhoneResult").css('display' , '');
            
            window.setTimeout(function(){
                $("#modifyPhoneResult").css('display' , 'none');
            }, 5000);
        }
    });
}

function deletePhone()
{
    if(!confirm("Are you sure you want to delete this phone?"))
        return;
    
    var postData = {};
    postData["action"] = "deletePhone";
    postData["phoneId"] = document.modifyPhone.phoneId.value;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                document.location = document.location + "/../";
            }
            else
                alert("Failed to delete phone: "+ response.message);
        }
    });
}

function watermarkPhone()
{
    if(!confirm("Are you sure you want to watermark this phone?"))
        return;
    
    $("#modifyPhoneResult").html("Watermarking this Phone");
    $("#modifyPhoneResult").css('display' , '');
            
    var postData = {};
    postData["action"] = "watermarkPhone";
    postData["phoneId"] = document.modifyPhone.phoneId.value;
    
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
                alert("Failed to watermark phone: "+ response.message);
        }
    });
}

function enableButton(btn, shouldEnable)
{
    if(shouldEnable)
    {
        // remove disabled class and attribute from btn
        if(btn.hasClass("disabled"))
        {
            btn.removeClass("disabled").removeAttr("disabled");
        }
    }
    else
    {
        // Disable Save button and add error message class to the message panel
        if(!btn.hasClass("disabled"))
        {
            btn.addClass("disabled").attr("disabled", "true");
        }
    }
}

function toggleModifyPhoneView(elem)
{
    var showNormal = (elem.value == "Show Normal View");
    
    if(showNormal)
    {
        // Phone Name
        $("#modifyPhone input:text").each(function() {
            var nameText = this.value;
            
            // hide input, display details as plaintext
            $("#modifyPhone input:text").css('display' , 'none');
            $("#modifyPhone input:text").after("<h1>" + nameText + "</h1>");
        });

        // Phone Description
        $("#modifyPhone textArea").each(function() {
            var descText = this.value;
            
            // hide input, display details as plaintext
            $("#modifyPhone textArea").css('display' , 'none');
            $("#modifyPhone textArea").after("<p>" + descText + "</p>");
        });
        
        // hide the labels
        $("#modifyPhone label").each(function() {
            this.style.display = 'none';
        });

        // Toggle button text
        elem.value = "Show Edit View";
    }
    else
    {
        // display labels and inputs, delete plaintext nodes
        $("#modifyPhone input:text").css('display' , '');
        $("#modifyPhone input:text").next().remove();
        
        $("#modifyPhone textArea").css('display' , '');
        $("#modifyPhone textArea").next().remove();
        
        // show the labels
        $("#modifyPhone label").each(function() {
            this.style.display = '';
        });
        
        // Toggle button text
        elem.value = "Show Normal View";
    }
}

function phoneDetailChanged()
{
     $("#modifyPhoneButton").removeClass("disabled");
}