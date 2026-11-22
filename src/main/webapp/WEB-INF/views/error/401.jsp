<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="401 - Non autorise"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="row justify-content-center mt-5">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm border-0 text-center p-4">
            <div class="card-body">
                <div class="display-1 text-warning fw-bold mb-3">401</div>
                <h2 class="card-title mb-3">Authentification requise</h2>
                <p class="text-muted mb-4">
                    <c:choose>
                        <c:when test="${not empty requestScope['jakarta.servlet.error.message']}">
                            <c:out value="${requestScope['jakarta.servlet.error.message']}"/>
                        </c:when>
                        <c:otherwise>
                            Vous devez etre authentifie pour acceder a cette ressource. Votre session a peut-etre expire.
                        </c:otherwise>
                    </c:choose>
                </p>
                <div class="d-flex justify-content-center gap-2">
                    <a href="${ctx}/login" class="btn btn-primary">
                        Se connecter
                    </a>
                    <a href="${ctx}/" class="btn btn-outline-secondary">
                        Page d'accueil
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
