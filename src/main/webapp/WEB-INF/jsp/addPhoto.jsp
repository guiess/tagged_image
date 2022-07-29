<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>VK Albums</title>
</head>
<body >

<h3>Add image with tag</h3>
<form:form action="${pageContext.request.contextPath}/photos/addPhotoWithTag">
    <table>
        <tr>
            <td><label>Url</label></td>
            <td><input type="text" name="imageUrl" /></td>
        </tr>
        <tr>
            <td><label>Tag</label></td>
            <td><input id="tagField" type="text" name="tag" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Add"/></td>
        </tr>
    </table>
</form:form>

<c:if test="${tags != null}">
    Existing tags (click to add): <br>
    <c:forEach items="${tags}" var="singleTag" varStatus="i">
        <u onclick="javascript:addTagToSearch('${singleTag}')">${singleTag}</u>
        <c:if test="${i.count % 5 == 0}">
            <br>
        </c:if>
    </c:forEach>
</c:if>

<c:if test="${result != null}">
    Result: ${result}<br>
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
