<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="403 - Acces refuse"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="row justify-content-center mt-5">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm border-0 text-center p-4">
            <div class="card-body">
                <div class="display-1 text-danger fw-bold mb-3">403</div>
                <h2 class="card-title mb-3">Acc&egrave;s interdit</h2>
                <p class="text-muted mb-4">
                    <c:choose>
                        <c:when test="${not empty requestScope['jakarta.servlet.error.message']}">
                            <c:out value="${requestScope['jakarta.servlet.error.message']}"/>
                        </c:when>
                        <c:otherwise>
                            Vous n'avez pas les autorisations necessaires pour acceder a cette page avec votre role actuel.
                        </c:otherwise>
                    </c:choose>
                </p>
                <div class="d-flex justify-content-center gap-2">
                    <a href="${ctx}/" class="btn btn-primary">
                        Retour a l'accueil
                    </a>
                    <button type="button" class="btn btn-outline-secondary" onclick="window.history.back();">
                        Page precedente
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
