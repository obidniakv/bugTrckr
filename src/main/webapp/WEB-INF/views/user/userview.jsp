<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<div class="breadcrumbs">
    <div class="row">
        <div class="col-sm-2 col-sm-offset-1">
            <h1 class="pull-left"> User Detail </h1>
        </div>
        <div class="col-sm-8">
            <ol class="pull-right breadcrumb">
                <li><a href="<spring:url value='/' />">Home</a></li>
                <li><a href="<spring:url value='/users' />">Users</a></li>
                <li class="active"> User Detail</li>
            </ol>
        </div>
    </div>
</div>

<div class="margin-top-30 row">
    <div class="col-sm-11 col-sm-offset-1 col-md-6 col-md-offset-1">

        <div class="row">
            <label class="col-sm-3">ID</label>
            <div class="col-sm-9"><p>${user.id}</p></div>
        </div>


        <div class="row">
            <label class="col-sm-3">Email</label>
            <div class="col-sm-9"><p>${user.email}</p></div>
        </div>

        <div class="row">
            <label class="col-sm-3">First name</label>
            <div class="col-sm-9"><p>${user.firstName}</p></div>
        </div>

        <div class="row">
            <label class="col-sm-3">Last name</label>
            <div class="col-sm-9"><p>${user.lastName}</p></div>
        </div>

        <div class="row">
            <label class="col-sm-3">Role</label>
            <div class="col-sm-9"><p>${user.role}</p></div>
        </div>

        <div class="row">
            <label class="col-sm-3">Description</label>
            <div class="col-sm-9">${user.description}</div>
        </div>

        <c:if test="${not empty user.project}">
            <div class="row">
                <label class="col-sm-3">Active Project</label>
                <div class="col-sm-9">${user.project.title}</div>
            </div>
        </c:if>
        <sec:authorize access="hasRole('ADMIN')">
            <div class="margin-top-30 row">
                <div class="col-sm-4">
                    <spring:url value="/user/${user.id}/edit" var="useredit"/>
                    <a class="btn btn-primary btn-u" href="${useredit}" role="button">Edit profile</a>
                </div>
                <div class="col-sm-8"></div>
            </div>
        </sec:authorize>
    </div>

    <div class="col-sm-11 col-sm-offset-1 col-md-3">
        <figure>
            <img src="data:image/jpg;base64,<c:out value='${user.encodedImage}'/>" class="img-thumbnail"
                 alt="${user.lastName}"/>
        </figure>
    </div>
</div>


<div class="margin-bottom-60">
    <div class="row"><p>&nbsp;</p></div>
</div>

<div class="row">
    <div class="user-history col-sm-10 col-sm-offset-1">
        <H3 class="text-center">User`s activity</H3>
        <ul>
            <c:forEach var="history" items="${allHistory.content}">
                <c:choose>
                    <c:when test="${history.action == 'CREATE_ISSUE'}">
                        <li>
                            <strong>
                                <c:choose>
                                    <c:when test="${history.changedByUser == null}">
                                        <span class="removed-user">Removed user</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<spring:url value='/user/${history.changedByUser.id}/view' />">${history.changedByUser.firstName} ${history.changedByUser.lastName}</a>
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            created issue <strong><a class="viewLink" href="<spring:url value='/issue/${history.issue.id}'/>">${history.issue.title}</a></strong>
                            and assigned it to
                            <strong>
                                <c:choose>
                                    <c:when test="${history.assignedToUser == null}">
                                        <span class="removed-user">Removed user</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<spring:url value='/user/${history.assignedToUser.id}/view' />">${history.assignedToUser.firstName} ${history.assignedToUser.lastName}</a>
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            <span class="createTime">at ${history.createTime}</span>
                        </li>
                    </c:when>
                    <c:when test="${history.action == 'CHANGE_ISSUE_STATUS'}">
                        <li>
                            <strong>
                                <c:choose>
                                    <c:when test="${history.changedByUser == null}">
                                        <span class="removed-user">Removed user</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<spring:url value='/user/${history.changedByUser.id}/view' />">${history.changedByUser.firstName} ${history.changedByUser.lastName}</a>
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            changed status of <strong><a class="viewLink" href="<spring:url value='/issue/${history.issue.id}'/>">${history.issue.title}</a></strong>
                            to <strong>${history.issueStatus}</strong>
                            <span class="createTime">at ${history.createTime}</span>
                        </li>
                    </c:when>
                    <c:when test="${history.action == 'CHANGE_ISSUE_ASSIGNEE'}">
                        <li>
                            <strong>
                                <c:choose>
                                    <c:when test="${history.changedByUser == null}">
                                        <span class="removed-user">Removed user</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<spring:url value='/user/${history.changedByUser.id}/view' />">${history.changedByUser.firstName} ${history.changedByUser.lastName}</a>
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            reassigned <strong><a class="viewLink" href="<spring:url value='/issue/${history.issue.id}'/>">${history.issue.title}</a></strong> to
                            <strong>
                                <c:choose>
                                    <c:when test="${history.assignedToUser == null}">
                                        <span class="removed-user">Removed user</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<spring:url value='/user/${history.assignedToUser.id}/view' />">${history.assignedToUser.firstName} ${history.assignedToUser.lastName}</a>
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            <span class="createTime">at ${history.createTime}</span>
                        </li>
                    </c:when>
                </c:choose>
            </c:forEach>
        </ul>
        <c:if test="${allHistory.getTotalPages()> 1}">
            <nav aria-label="Page navigation" id="pagerID">
                <div class="text-center">
                    <ul class="pagination">
                        <li>
                            <a href="<spring:url value='/user/${user.id}/view?page=0'/>" aria-label="Start">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        <c:forEach var="page" begin="0" end="${allHistory.getTotalPages() - 1}">
                            <li>
                                <a href="<spring:url value='/user/${user.id}/view?page=${page}'/>">${page + 1}</a>
                            </li>
                        </c:forEach>
                        <li>
                            <a href="<spring:url value='/user/${user.id}/view?page=${allHistory.getTotalPages() - 1}'/>"
                               aria-label="End"><span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>
        </c:if>
    </div>
</div>

