<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <h1>Sale Item Creation Form</h1>
    <form name="fsaleUploadForm" 
          id="fsaleUploadForm"
          method="post"
          enctype="multipart/form-data" 
          action="uploadHandler.jsp"
    >
        <!-- Item Name -->
        <label for="saleTitle">Name: </label> 
        <input type="text" name="saleTitle" id="saleTitle" tabindex="1" maxlength="64"/>

        <!-- Item Price -->
        <label for="salePrice">Price: </label> 
        <input type="text" name="salePrice" id="salePrice" tabindex="2"/>

        <!-- Item Description -->
        <label for="saleDescription">Description: </label> 
        <textarea name="saleDescription" id="saleDescription" tabindex="3"></textarea>

        <p style="line-height: 0.75em;">To add a link: '&lt;a href="URL" target="_blank"&gt;LINK NAME&lt;/a&gt;'</p>
        <p style="line-height: 0.75em;">To add a new line: '&lt;br/&gt;'</p>

        <applet id="jumpLoaderApplet" name="jumpLoaderApplet"
                code="jmaster.jumploader.app.JumpLoaderApplet.class"
                archive="/admin/applets/jumploader_z.jar"
                width="516"
                height="300" 
                mayscript>
            <param name="uc_uploadUrl" value="/Admin/Upload/SaleItem"/>
            <param name="ac_fireAppletInitialized" value="true"/>
            <param name="ac_fireUploaderStatusChanged" value="true"/>
        </applet>
        
        <div id="messagePanel"></div>
        
        <div>
            <input id="createSaleItemButton" class="button" type="button" value="Create Sale Item" onclick="saveSaleItem();"/>
            <input class="button" type="reset" value="Clear" onclick="clearSaleItemForm();"/>
        </div>
    </form>
</div>
