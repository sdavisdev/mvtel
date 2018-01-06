<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${param.hide_numbers ne true}">
    <div align="center" style="line-height:1.5em;">
        <c:if test="${current_page ne 1}">
            <a style="font-size:0.85em; text-decoration:none;" href="/items/${category_name}?current_page=${current_page-1}">Previous</a>
            &nbsp;&nbsp;
        </c:if>
        <c:forEach var="pageNum" begin="1" end="${total_pages}">
            <a style="text-decoration:none;" href="/items/${category_name}?current_page=${pageNum}">
                <c:if test="${pageNum eq current_page}"><span style="color:#774715; font-weight:bold; text-decoration:underline">${pageNum}</span></c:if>
                <c:if test="${pageNum ne current_page}">${pageNum}</c:if>
            </a>
            &nbsp;
        </c:forEach>
        <c:if test="${current_page ne total_pages}">
            &nbsp;
            <a style="font-size:0.85em; text-decoration:none;" href="/items/${category_name}?current_page=${current_page+1}">Next</a>
        </c:if>
    </div>
</c:if>

<c:if test="${param.hide_words ne true}">
    <p>Displaying results ${category_items_begin + 1} through ${category_items_end + 1} of ${category_items_total} ${category_name}</p>
</c:if>
<!--
<p>Page Number ${current_page} of ${total_pages}</p>
-->