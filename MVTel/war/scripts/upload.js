/**
 * Javascript functions for images uploads.
 */
var isPhoneUpload = (window.location.search.indexOf("UploadPhone") >= 0);

var errorClassName = "error";

var saleItemDirName=0;

function appletInitialized(applet) 
{
    // Limit file types to jpeg for phone upload. Acutally do it for phone or sale item uploads
    // if(isPhoneUpload)
        document.jumpLoaderApplet.getUploaderConfig().setFileNamePattern('\\d+\\.((jpg)|(JPG)|(jpeg)|(JPEG))$');
    
    // Hide the upload button panel
    document.jumpLoaderApplet.getViewConfig().setUploadViewStartActionVisible(false);
    document.jumpLoaderApplet.getMainView().getUploadView().updateView();
}

/**
 * file status changed notification
 */
function uploaderStatusChanged(uploader, file) 
{
    if(document.imageUploadForm)
    {
        if (uploader.getStatus() == 1)
        {
        	alert("Status Changed");
            var attrSet = uploader.getAttributeSet();
            var name        = attrSet.createStringAttribute("phoneTitle",       document.imageUploadForm.phoneTitle.value);
            var category    = attrSet.createStringAttribute("phoneCategory",    document.imageUploadForm.phoneCategory.value);
            var description = attrSet.createStringAttribute("phoneDescription", document.imageUploadForm.phoneDescription.value);
            var numImages   = attrSet.createStringAttribute("numImages",        uploader.getFileCount());
            var blobstore   = attrSet.createStringAttribute("blobstore",        "REMOVE_ME");
            name.setSendToServer(true);
            category.setSendToServer(true);
            description.setSendToServer(true);
            numImages.setSendToServer(true);
            blobstore.setSendToServer(true);
        }

        // upload finished
        if (uploader.getStatus() == 0)
        {
            watermarkPhone();
        }
    }
    else if(document.fsaleUploadForm)
    {
        if (uploader.getStatus() == 1)
        {
            var dirNameAttr = uploader.getAttributeSet().createStringAttribute("dirName", "" + saleItemDirName);
            dirNameAttr.setSendToServer(true);
        }

        // upload finished
        if (uploader.getStatus() == 0)
        {
            watermarkSaleItem();
        }
    }
}

function watermarkPhone()
{
    showMessage("Watermarking this phone. Please wait a few moments.", false);
            
    var postData = {};
    postData["action"] = "watermarkPhone";
    postData["phoneTitle"] = document.imageUploadForm.phoneTitle.value;
    postData["phoneCategory"] = document.imageUploadForm.phoneCategory.value;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                showMessage("Watermarking Complete!", false);
                
                var saveButton = $("#imageUploadForm div input#createPhoneButton");
                enableButton(saveButton, true);

                checkPhoneExists(document.imageUploadForm.phoneCategory.value, 
                                 document.imageUploadForm.phoneTitle.value, 
                                 "phoneExistsResponsePostUpload");
            }
            else
                alert("Failed to watermark phone: "+ response.message);
        }
    });
}

function watermarkSaleItem()
{
    showMessage("Watermarking the sale item images. Please wait a few moments.", false);
            
    var postData = {};
    postData["action"] = "watermarkSaleItem";
    postData["saleItemId"] = saleItemDirName;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                showMessage("Watermarking Complete!", false);
                
                var name = document.getElementById("saleTitle").value;
                var link = "../items/For+Sale/#" + saleItemDirName;
                var message = "Upload Complete! " 
                    + "<a target='_blank' href='" + link + "'><b>View " + name + "<b/></a>.";
                
                clearSaleItemForm();
                showMessage(message, false);
            }
            else
                alert("Failed to watermark this sale item: "+ response.message);
        }
    });
}

function removeAllFiles(uploader)
{
    var files = uploader.getAllFiles();

    for (var i = 0; i < files.length; i++)
    {
        uploader.removeFile(files[i]);
    }
}

function clearPhoneForm()
{
    removeAllFiles(document.jumpLoaderApplet.getUploader());
    $('form')[0].reset();
    clearMessage();
    enableButton($("#imageUploadForm div input#createPhoneButton"), true);
}

function clearSaleItemForm()
{
    removeAllFiles(document.jumpLoaderApplet.getUploader());
    $('form')[0].reset();
    clearMessage();
    enableButton($("#fsaleUploadForm div input#createSaleItemButton"), true);
}

function startPhoneUpload()
{
    if(validateFormFields() && validateImages(true))
    {
        showMessage("Uploading...", false);
        document.jumpLoaderApplet.getUploader().startUpload();
        
        var saveButton = $("#imageUploadForm div input#createPhoneButton");
        enableButton(saveButton, false);
    }
}

function validateSaleItemForm()
{
    var saleTitle = document.getElementById("saleTitle").value;
    var salePrice = document.getElementById("salePrice").value;
    var saleDescription = document.getElementById("saleDescription").value;
    var numImages = document.jumpLoaderApplet.getUploader().getFileCount();
    
    var alertText = "";
    if(saleTitle.length == 0)
        alertText += "Please enter a title.\n";
    if(salePrice == "")
        alertText += "Please enter a price.\n";
    if(saleDescription == "")
        alertText += "Please enter a description.\n";
    
    if(alertText != "")
    {
        alert(alertText);
        return false;
    }
    
    if(numImages == 0)
    {
        return confirm("Are you sure you want to upload without any images?");
    }
    
    return true;
}

function saveSaleItem()
{
    if(!(validateSaleItemForm() && validateImages(false)))
        return;
    
    var saveButton = $("#fsaleUploadForm div input#createSaleItemButton");
    enableButton(saveButton, false);
    
    showMessage("Saving sale item to the database.", false);
            
    var postData = {};
    postData["action"] = "saveSaleItem";
    postData["saleTitle"] = document.fsaleUploadForm.saleTitle.value;
    postData["salePrice"] = document.fsaleUploadForm.salePrice.value;
    postData["saleDescription"] = document.fsaleUploadForm.saleDescription.value;
    postData["numImages"] = document.jumpLoaderApplet.getUploader().getFileCount();
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            if(response.success)
            {
                saleItemDirName = response.message;
                
                if(document.jumpLoaderApplet.getUploader().getFileCount() > 0)
                {
                    startSaleItemUpload();
                }
                else 
                {
                    var name = document.getElementById("saleTitle").value;
                    var link = "../items/For+Sale/" + saleItemDirName;
                    var message = "Upload Complete! " 
                        + "<a target='_blank' href='" + link + "'><b>View " + name + "<b/></a>.";

                    clearSaleItemForm();
                    showMessage(message, false);
                }
            }
            else
            {
                alert("Failed to save sale item: "+ response.message);
                enableButton(saveButton, true);
            }
        }
    });
}

function startSaleItemUpload()
{
    showMessage("Uploading Images...", false);
    document.jumpLoaderApplet.getUploader().startUpload();

//    var saveButton = $("#imageUploadForm div input#createPhoneButton");
//    enableButton(saveButton, false);
}

function checkPhoneExistsNoArg()
{
    var category = $('#phoneCategory').get(0).value;
    var phone = $('#phoneTitle').get(0).value;
    
    checkPhoneExists(category, phone, "phoneExistsResponsePreUpload");
}

function checkPhoneExists(category, name, callbackFunc)
{
    if(category == "" || name == "")
    {
        phoneExistsResponsePreUpload(category, name, false, "")
        return;
    }
    
    var postData = {};
    postData["action"] = "checkPhoneExists";
    postData["phoneCategory"] = category;
    postData["phoneName"] = name;
    
    $.ajax({
        url: "/Admin/",
        type: "POST",
        data: postData,
        dataType: "json",
        context: document.main,
        success: function(response){
            jQuery.globalEval(callbackFunc + "('" + category + "', '" + name + "', " + response.success + ", '" + response.message + "');");
        }
    });
    
}

/**
 * Handler Callback to checkPhoneExists().
 * 
 * This ensures the same phone doesn't get uploaded twice.
 * It shows an error message if phone exists
 */
function phoneExistsResponsePreUpload(category, name, exists, message)
{
    var saveButton = $("#imageUploadForm div input#createPhoneButton");
    if(exists)
    {
        // phone already exists, display error message and disable button
        var errorMsg = "A phone named '" + name + "' already exists under category '" 
            + category + "' -- Please select another name.";

        showMessage(errorMsg, true);
        enableButton(saveButton, false);
    }
    else
    {
        // Enable Save button and clear the message panel
        clearMessage();
        enableButton(saveButton, true);
    }
}

/**
 * Handler Callback to checkPhoneExists().
 * 
 * This checks to make sure phone has been create successfully following upload.
 * It shows a link to view the newly created phone if it exists.
 * It shows an error message if phone does not exist.
 */
function phoneExistsResponsePostUpload(category, name, exists, message)
{
    if(exists)
    {
        var link = "../items/" + category + "/" + name + "/";
        message = "Upload Complete! " 
                + "<a target='_blank' href='" + link + "'><b>View " + name + "<b/></a>.";
        
        clearPhoneForm();
        showMessage(message, false);
    }
    else
    {
        showMessage("Upload Failed. Please try again.", true);
    }
    
}

function showMessage(message, isError)
{
    $('#messagePanel').html(message);
    
    if(isError)
    {
        if(!$('#messagePanel').hasClass(errorClassName))
            $('#messagePanel').addClass(errorClassName);
    }
    else
    {
        if($('#messagePanel').hasClass(errorClassName))
            $('#messagePanel').removeClass(errorClassName);
    }
}

function clearMessage()
{
    $('#messagePanel').html("");
    
    if($('#messagePanel').hasClass(errorClassName))
        $('#messagePanel').removeClass(errorClassName);
}

/**
 * Validate each of the form fields.
 */
function validateFormFields()
{
    var phoneTitle = document.getElementById("phoneTitle").value;
    var phoneCategory = document.getElementById("phoneCategory").value;
    var phoneDescription = document.getElementById("phoneDescription").value;
    
    var alertText = "";
    if(phoneTitle.length == 0)
        alertText += "Please enter a phone title.\n";
    if(phoneCategory == "")
        alertText += "Please select a category.\n";
//    if(phoneDescription == "")
//        alertText += "Please enter a description.\n";
    
    if(alertText != "")
    {
        alert(alertText);
        return false;
    }
    
    return true;
}

/**
 * Images are defined in the form of 1-n.jpg|jpeg.
 * Validate that from 1 to the number of images we have an image file
 * with the appropriate name.
 */
function validateImages(requireImages)
{
    var files = document.jumpLoaderApplet.getUploader().getAllFiles();
    var numFiles = files.length;
    
    // alert user if no files are specified
    if(numFiles == 0 && requireImages)
    {
        alert("There are zero images selected. Please add some images.")
        return false;
    }
    
    // Iterare through each of the files and determine if they are
    // in the appropriate sequence of 1 to n.
    
    // track each of the found files in foundFiles.
    var foundFiles = new Array();
    for(var i=0; i < numFiles; i++)
    {
        foundFiles[i] = false;
    }
    
    // mark the file index true when file is encountered
    for(var i=0; i < numFiles; i++)
    {
        var fileName = files[i].getName();
        
        var fileNumber = parseInt(fileName.substring(0, fileName.indexOf('.')));
        foundFiles[fileNumber-1] = true;
    }
    
    // determine any files that are missing and alert user if any are
    var alertText = "";
    var shouldAlert = false;
    for(var i=0; i < foundFiles.length; i++)
    {
        if(foundFiles[i] == undefined)
        {
            foundFiles[i] = false;
        }
        
        if(foundFiles[i] == false)
            shouldAlert = true;
            
        alertText += (i+1) + ": " +  (foundFiles[i] ? "OK" : "MISSING") + "\n";
    }
    
    if(shouldAlert)
    {
        alert("Images must be specified in numeric order starting from 1.\n"
            + "Please correct the missing files and try again:\n\n" + alertText);
        return false;
    }
    
    return true;
}
