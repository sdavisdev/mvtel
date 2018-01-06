<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<--
 -
 -
 - THIS FILE IS NOT USED
 -
 -
 -
 -->
            <!-- If logged in, set up text box and hidden fields for editing. -->
            <c:if test="${sessionScope.authenticated}">
                <form name="modifyPhone" id="modifyPhone" action="/Admin/" method="POST">
                    <input name="action" type="hidden" value="modifyPhone"/>
                    <input name="phoneId" type="hidden" value="${phone.id}"/>
                    <input name="phoneDirectory" type="hidden" value="${phone.directory}"/>
                    <input name="phoneImgCount" type="hidden" value="${phone.imageCount}"/>
                    <input name="phoneCategory" type="hidden" value="${phone.category}"/>
                    <label style="display:none;">Name: </label>
                    <input style="display:none;" name="phoneName" type="text" value="${phone.name}" onkeyup="phoneDetailChanged();"  maxlength="64"/>
            </c:if>
            <!-- End Login Check -->

            <div class="product-area" id="fs${item.id}"> 
                <h2>${item.title} - &dollar;${item.price}</h2>

                <div class="product-image">
                    <a rel="shadowbox[${item.title}];" href="/for_sale/${item.id}/1.jpg">
                        <img src="/for_sale/${item.id}/1-medium.jpg" border="0" alt="" />
                    </a>
                </div>

                <div class="product-info" id="${item.id}">
                    <div class="description">

                        <!-- If logged in, setup textArea for editing.-->
                        <c:choose>
                            <c:when test="${sessionScope.authenticated}">
                                <label style="display:none;">Description: </label>
                                <textarea name="phoneDesc" style="display:none;" onkeyup="phoneDetailChanged();">${item.description}</textarea>
                                <p>${item.description}</p>
                                <label style="display:none;">To add a link: '&lt;a href="URL" target="_blank"&gt;LINK NAME&lt;/a&gt;'</label>
                                <label style="display:none;"> <br/>To add a new line: '&lt;br/&gt;'<br/></label>
                            </c:when>
                            <c:otherwise>
                                <p>${item.description}</p>
                            </c:otherwise>
                        </c:choose>
                        <!-- End Login Check -->
                    </div>
                    <ul class="additional-images">
                        <c:forEach var="i" begin="2" end="${item.numImages}">
                            <li>
                                <a rel="shadowbox[${item.title}];" href="/for_sale/${item.id}/${i}.jpg">
                                    <img src="/for_sale/${item.id}/${i}-thumb.jpg" border="0" alt="" />
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="clear-left"></div>
            </div>
    
            <!-- If logged in, present save button. -->
            <c:if test="${sessionScope.authenticated}">
                    <br/>
                    <input id="modifyPhoneButton" class="disabled button" type="button" value="Save Changes" onclick="savePhone();"/>
                    <input id="modifyPhoneToggle" class="button" type="button" value="Show Edit View" onclick="toggleModifyPhoneView(this);"/>
                    <input id="watermarkPhoneButton" class="button" type="button" value="(Re)Watermark Phone" onclick="watermarkPhone();"/>
                    <input id="deleteSaleItemButton" class="button" type="button" value="Delete Item" onclick="deleteSaleItem(${item.id});"/>
                    <p id="modifyPhoneResult" style="display:none;"/>
                </form>
            </c:if>