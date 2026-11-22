<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Cuisine"/>
<c:set var="refresh" value="30"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Cuisine</h2>
    <a href="${ctx}/cuisine" class="btn btn-outline-secondary">Actualiser</a>
</div>

<c:if test="${empty commandes}">
    <p class="text-center text-muted">Aucune commande a preparer.</p>
</c:if>

<div class="row g-3">
    <c:forEach var="c" items="${commandes}">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 ${c.statut == 'EN_PREPARATION' ? 'border-warning' : 'border-secondary'}">
                <div class="card-header d-flex justify-content-between">
                    <strong>Table <c:out value="${c.numeroTable}"/></strong>
                    <span class="badge ${c.statut == 'EN_PREPARATION' ? 'bg-warning text-dark' : 'bg-secondary'}">
                        <c:out value="${c.statut}"/>
                    </span>
                </div>
                <div class="card-body">
                    <p class="text-muted small mb-2">
                        Commande n&deg; <c:out value="${c.idCommande}"/> - <c:out value="${c.dateFormatee}"/>
                    </p>
                    <ul class="list-unstyled mb-0">
                        <c:forEach var="d" items="${c.details}">
                            <li><c:out value="${d.quantite}"/> x <c:out value="${d.plat.nom}"/></li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="card-footer">
                    <form method="post" action="${ctx}/cuisine">
                        <input type="hidden" name="idCommande" value="${c.idCommande}">
                        <c:choose>
                            <c:when test="${c.statut == 'EN_ATTENTE'}">
                                <input type="hidden" name="nouveau" value="EN_PREPARATION">
                                <button type="submit" class="btn btn-warning w-100">Demarrer</button>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="nouveau" value="PRETE">
                                <button type="submit" class="btn btn-success w-100">Pr&ecirc;te</button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>