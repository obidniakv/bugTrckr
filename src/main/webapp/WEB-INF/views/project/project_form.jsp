<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:choose>
    <c:when test="${formaction eq 'new'}">
        <c:set var="breadcrumsname" scope="session" value="Add project"/>
        <c:set var="buttonname" scope="session" value="Create"/>
    </c:when>
    <c:otherwise>
        <c:set var="breadcrumsname" scope="session" value="Update project"/>
        <c:set var="buttonname" scope="session" value="Update"/>
    </c:otherwise>
</c:choose>

<div class="breadcrumbs">
    <div class="row">
        <div class="col-sm-2 col-sm-offset-1">
            <h1 class="pull-left"> ${breadcrumsname} </h1>
        </div>
        <div class="col-sm-8">
            <ol class="pull-right breadcrumb">
                <li><a href="<spring:url value='/'/>">Home</a></li>
                <li><a href="<spring:url value='/projects'/>">Projects</a></li>
                <li class="active"> ${breadcrumsname} </li>
            </ol>
        </div>
    </div>
</div>

<div class="margin-top-30">
    <form:form action="/projects/add" modelAttribute="project" method="POST">
        <div class="col-sm-5 col-sm-offset-1">
            <spring:bind path="title">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label for="titleInput">Project title</label>
                    <form:input path="title" type="text" class="form-control" id="titleInput"
                                placeholder="Project Title"/>
                    <form:errors path="title" class="control-label"/>
                </div>
            </spring:bind>

            <label class="margin-top-30">
                <form:checkbox id="guest" path="guestView" value="true"/>&nbsp&nbspEnable project review by guests
            </label>

            <div id="enableView">
                <label class="margin-top-30"><form:checkbox id="issue" path="guestCreateIssues" value="true"/>
                    &nbsp&nbspEnable creation of issue by guests</label>
                <label class="margin-top-30"><form:checkbox id="comment" path="guestAddComment" value="true"/>
                    &nbsp&nbspEnable commenting of issues in project</label>
            </div>
        </div>

        <form:hidden path="id"/>

        <spring:bind path="description">
            <div class="margin-top-30 col-sm-10 col-sm-offset-1 form-group ${status.error ? 'has-error' : ''}">
                <label for="editor1">Description</label><br/>
                <form:errors path="description" class="control-label"/>
                <form:textarea path="description" cols="100" id="editor1" rows="10"></form:textarea>
            </div>
        </spring:bind>

        <div class="col-sm-10 col-sm-offset-1">
            <input type="submit" value="${buttonname}" class="margin-top-30 btn-u pull-right"/>
        </div>
    </form:form>
</div>