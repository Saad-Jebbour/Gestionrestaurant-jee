<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Statistiques"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<h2 class="mb-3">Statistiques</h2>

<div class="row g-4">
    <div class="col-md-6">
        <h4>Chiffre d'affaires des 7 derniers jours</h4>
        <table class="table table-striped bg-white">
            <thead><tr><th>Jour</th><th class="text-end">Chiffre d'affaires</th></tr></thead>
            <tbody>
                <c:forEach var="l" items="${caParJour}">
                    <tr>
                        <td><c:out value="${l[0]}"/></td>
                        <td class="text-end">
                            <fmt:formatNumber value="${l[1]}" minFractionDigits="2" maxFractionDigits="2"/> DH
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty caParJour}">
                    <tr><td colspan="2" class="text-center text-muted">Aucune vente sur la periode</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <div class="col-md-6">
        <h4>Top 5 des plats</h4>
        <table class="table table-striped bg-white">
            <thead><tr><th>#</th><th>Plat</th><th class="text-end">Quantite vendue</th></tr></thead>
            <tbody>
                <c:forEach var="l" items="${topPlats}" varStatus="st">
                    <tr>
                        <td><c:out value="${st.count}"/></td>
                        <td><c:out value="${l[0]}"/></td>
                        <td class="text-end"><c:out value="${l[1]}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty topPlats}">
                    <tr><td colspan="3" class="text-center text-muted">Aucune donnee</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
