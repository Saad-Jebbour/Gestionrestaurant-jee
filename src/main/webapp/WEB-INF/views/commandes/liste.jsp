<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Commandes"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Commandes</h2>
    <a href="${ctx}/commandes/nouvelle" class="btn btn-primary">Nouvelle commande</a>
</div>

<form method="get" action="${ctx}/commandes" class="row g-2 mb-3">
    <div class="col-md-3">
        <input type="date" name="date" class="form-control" value="${param.date}">
    </div>
    <div class="col-md-3">
        <select name="statut" class="form-select">
            <option value="">Tous les statuts</option>
            <c:forEach var="s" items="${statuts}">
                <option value="${s}" ${param.statut == s ? 'selected' : ''}>${s}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2">
        <input type="number" name="numeroTable" min="1" class="form-control" placeholder="Table" value="${param.numeroTable}">
    </div>
    <div class="col-md-2">
        <button type="submit" class="btn btn-outline-primary">Filtrer</button>
    </div>
</form>

<table class="table table-striped bg-white">
    <thead><tr><th>N&deg;</th><th>Date</th><th>Table</th><th>Statut</th><th>Total</th><th></th></tr></thead>
    <tbody>
        <c:forEach var="c" items="${commandes}">
            <tr>
                <td><c:out value="${c.idCommande}"/></td>
                <td><c:out value="${c.dateFormatee}"/></td>
                <td><c:out value="${c.numeroTable}"/></td>
                <td><c:out value="${c.statut}"/></td>
                <td><fmt:formatNumber value="${c.total}" minFractionDigits="2" maxFractionDigits="2"/> DH</td>
                <td><a class="btn btn-sm btn-outline-secondary" href="${ctx}/commandes/detail?id=${c.idCommande}">Voir</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty commandes}">
            <tr><td colspan="6" class="text-center text-muted">Aucune commande</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
