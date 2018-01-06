var emailUrl = "/Email/";

function registerEmail()
{
    var emailName = $("#emailName").prop("value");
    var emailAddress = $("#emailAddress").prop("value");
    
    var alertText = "";
    if("" == emailAddress)
    {
        alertText += "Please Enter an Email Address.\n";
    }
    if("" == emailName)
    {
        alertText += "Please Enter Your Name.\n";
    }
    
    if("" != alertText)
    {
    	alert(alertText);
    	return;
    }
    
    var postData = {};
    postData["action"] = "register";
    postData["email"] = emailAddress;
    postData["name"] = emailName;
    
    $.ajax({
        url: emailUrl,
        type: "POST",
        data: postData,
        dataType: "json",
        success: function(response){
            if(response.success)
            {
                alert(emailName + ", thank you. Your email address '" + emailAddress + "' has been registered. Thank you!");
            }
            else
                alert("Failed register your email address: "+ response.message);
        }
    });
}

function unregisterEmail(emailAddress)
{
    
    var postData = {};
    postData["action"] = "unregister";
    postData["email"] = emailAddress;
    
    $.ajax({
        url: emailUrl,
        type: "POST",
        data: postData,
        dataType: "json",
        success: function(response){
            if(response.success)
            {
                alert("Email address '" + response.message + "' has been unregistered.");
                return true;
            }
            else
            {
                alert("Failed unregister email address: "+ response.message);
                return false;
            }
        }
    });
}

function unsubscribeEmail()
{
    
    var postData = {};
    postData["action"] = "unregister";
    postData["email"] = $("#emailAddress").prop("value");

    $.ajax({
        url: emailUrl,
        type: "POST",
        data: postData,
        dataType: "json",
        success: function(response){
            if(response.success)
            {
                alert("Your email address: "+ postData["email"] + " has been unsubscribed.");
            	document.location = "/";
                return true;
            }
            else
            {
                alert("Failed unregister email address: "+ response.message);
                return false;
            }
        }
    });
}

function publishEmail()
{
    var emailSubject = $("#emailSubject").prop("value");
    var emailMessage = $("#emailMessage").prop("value");
    
    if(emailSubject == '' || emailMessage == '')
    {
    	alert("The message subject and/or email or empty. Fill these in.");
    	return;
    }
    
    var postData = {};
    postData["action"] = "publish";
    postData["subject"] = emailSubject;
    postData["message"] = emailMessage;
    
    $.ajax({
        url: emailUrl,
        type: "POST",
        data: postData,
        dataType: "json",
        success: function(response){
            if(response.success)
            {
                alert("Email Sent Successfully.");
            }
            else
                alert("Failed send email: "+ response.message);
        }
    });
}

function receiveEmail()
{
    var emailSubject = $("#emailSubject").prop("value");
    var emailMessage = $("#emailMessage").prop("value");
    var emailAddress = $("#emailAddress").prop("value");
    var emailName    = $("#emailName").prop("value");
    
    // validate
    var message="";
    if(emailSubject == "")
        message += "Subject\n";
    if(emailMessage == "")
        message += "Message\n";
    if(emailAddress == "")
        message += "Your Email\n";
    if(emailName == "")
        message += "Your Name";
    
    if(message != "")
    {
        alert("Please fill in the following information:\n" + message);
        return;
    }
    // end validation
    
    var postData = {};
    postData["action"] = "receive";
    postData["subject"] = emailSubject;
    postData["message"] = emailMessage;
    postData["senderEmail"] = emailAddress;
    postData["senderName"] = emailName;
    
    $("#publishEmailButton").val("Sending Message...");
    $("#publishEmailButton").addClass("disabled");
    
    $.ajax({
        url: emailUrl,
        type: "POST",
        data: postData,
        dataType: "json",
        success: function(response){
            if(response.success)
            {
                alert("Message Sent Successfully.");
                
                // close the popup
                if(parent.Shadowbox)
                    parent.Shadowbox.close();
            }
            else
                alert("Failed to send message: "+ response.message);
        },
        complete: function(response){
            $("#publishEmailButton").val("Send Email");
            $("#publishEmailButton").removeClass("disabled");
        }
    });
}

function previewEmail()
{
    var emailSubject = $("#emailSubject").prop("value");
    var emailMessage = $("#emailMessage").prop("value");
    window.open("/Email/?action=preview&subject=" + emailSubject + 
            "&message=" + emailMessage, "_blank");
}

function deleteEmailAddress(emailAddress)
{
    if(!confirm("Are you sure you want to delete: " + emailAddress + "?"))
        return;
    
    var deleteResult = unregisterEmail(emailAddress);
    
    
}