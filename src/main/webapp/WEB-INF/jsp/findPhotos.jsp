<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>VK Albums</title>
</head>
<body >

<h3>Find photos by tag</h3>
<form:form action="${pageContext.request.contextPath}/photos/findPhotosByTag">
    <table>
        <tr>
            <td><label>Tag</label></td>
            <td><input id="tagField" type="text" name="tag" value="${tag}" />
                <button type="button" onclick="javascript:clearTagField()">Clear</button></td>
        </tr>
        <tr>
            <td><input type="submit" value="Find"/></td>
        </tr>
    </table>
</form:form>

<c:if test="${result != null}">
    Result: ${result}<br>
</c:if>

<c:if test="${photos != null}">

    <table>
        <tbody>
        <tr>
        <c:forEach items="${photos}" var="photo" varStatus="i">
                <td>
                    <a href='#' onclick='javascript:window.open("${photos[i.index].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                        <img src="${photos[i.index].previewPhotoURI != null? photos[i.index].previewPhotoURI: photos[i.index].photoURI}" height="100" alt="">
                    </a> <br>
                    ${photos[i.index].tags}
                </td>
            <c:if test="${i.count % 5 == 0}">
                </tr><tr>
            </c:if>
        </c:forEach>
        </tr>
        </tbody>
    </table> <br>
</c:if>

<c:if test="${tags != null}">
    Existing tags (click to add): <br>
        <c:forEach items="${tags}" var="singleTag" varStatus="i">
            <u onclick="javascript:addTagToSearch('${singleTag}')">${singleTag}</u>
            <c:if test="${i.count % 5 == 0}">
                <br>
            </c:if>
        </c:forEach>
</c:if>

<c:if test="${error != null}">
    Error: ${error}<br>
</c:if>

<script type="text/javascript">
    function addTagToSearch(tag)
    {
        document.getElementById("tagField").value = document.getElementById("tagField").value + ' ' + tag
    }

    function clearTagField()
    {
        document.getElementById("tagField").value = ''
    }
</script>

</body>
</html>
