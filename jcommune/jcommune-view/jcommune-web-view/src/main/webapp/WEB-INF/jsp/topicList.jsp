<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Forum</title>
</head>
<body>
<form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/create.html" method="GET">
    <table border="1" width="100%">
        <tr>
            <td width="80%"><spring:message code="label.topic"/></td>
            <td width="10%"><spring:message code="label.author"/></td>
            <td width="10%"><spring:message code="label.date"/></td>
        </tr>

        <c:forEach var="topic" items="${topics}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branch/${branchId}/topic/${topic.id}.html"> <c:out
                        value="${topic.title}"/></a></td>
                <td><c:out value="${topic.topicStarter.username}"/></td>
                <td><joda:format value="${topic.lastModificationDate}"
                                 locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                 pattern="dd MMM yyyy HH:mm"/></td>
            </tr>
        </c:forEach>
    </table>
    <br>
    <sec:authorize access="isAuthenticated()">
        <input type="submit" value="<spring:message code="label.addtopic"/>"/>
    </sec:authorize>
</form:form>

<div id="pagination">
    <c:if test="${maxPages > 1}">
        <c:if test="${page > 1}">
            <c:url value="/branch/${branchId}.html" var="prev">
                <c:param name="page" value="${page - 1}"/>
            </c:url>
            <a href='<c:out value="${prev}" />' class="pn next"><spring:message code="pagination.prev"/></a>
        </c:if>
        <c:forEach begin="1" end="${maxPages}" step="1" varStatus="i">
            <c:choose>
                <c:when test="${page == i.index}">
                    <span>${i.index}</span>
                </c:when>
                <c:otherwise>
                    <c:url value="/branch/${branchId}.html" var="url">
                        <c:param name="page" value="${i.index}"/>
                    </c:url>
                    <a href='<c:out value="${url}" />'>${i.index}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page + 1 < maxPages+1}">
            <c:url value="/branch/${branchId}.html" var="next">
                <c:param name="page" value="${page + 1}"/>
            </c:url>
            <a href='<c:out value="${next}" />' class="pn next"><spring:message code="pagination.next"/></a>
        </c:if>
    </c:if>
</div>
</body>
</html>