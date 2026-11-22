<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Commande"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="role" value="${sessionScope.user.role}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-2">
    <h2>Commande n&deg; <c:out value="${commande.idCommande}"/> - Table <c:out value="${commande.numeroTable}"/></h2>
    <span class="badge bg-secondary fs-6"><c:out value="${commande.statut}"/></span>
</div>
<p class="text-muted">Creee le <c:out value="${commande.dateFormatee}"/></p>

<table class="table table-striped bg-white">
    <thead>
        <tr><th>Plat</th><th>Quantite</th><th>Prix unitaire</th><th>Sous-total</th><th></th></tr>
    </thead>
    <tbody>
        <c:forEach var="d" items="${commande.details}">
            <tr>
                <td><c:out value="${d.plat.nom}"/></td>
                <td><c:out value="${d.quantite}"/></td>
                <td><fmt:formatNumber value="${d.prixUnitaire}" minFractionDigits="2" maxFractionDigits="2"/> DH</td>
                <td><fmt:formatNumber value="${d.sousTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH</td>
                <td>
                    <c:if test="${commande.statut == 'EN_ATTENTE'}">
                        <form method="post" action="${ctx}/commandes/retirer" class="d-inline">
                            <input type="hidden" name="idCommande" value="${commande.idCommande}">
                            <input type="hidden" name="idDetail" value="${d.idDetail}">
                            <button type="submit" class="btn btn-sm btn-outline-danger">Retirer</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty commande.details}">
            <tr><td colspan="5" class="text-center text-muted">Aucun plat pour le moment</td></tr>
        </c:if>
    </tbody>
    <tfoot>
        <tr>
            <th colspan="3" class="text-end">Total</th>
            <th colspan="2"><fmt:formatNumber value="${commande.total}" minFractionDigits="2" maxFractionDigits="2"/> DH</th>
        </tr>
    </tfoot>
</table>

<%-- Ajout d'un plat : seulement tant que la commande est EN_ATTENTE --%>
<c:if test="${commande.statut == 'EN_ATTENTE'}">
    <form method="post" action="${ctx}/commandes/ajouter" class="row g-2 mb-4">
        <input type="hidden" name="idCommande" value="${commande.idCommande}">
        <div class="col-md-5">
            <select name="idPlat" class="form-select" required>
                <c:forEach var="p" items="${plats}">
                    <option value="${p.idPlat}"><c:out value="${p.nom}"/> - <fmt:formatNumber value="${p.prix}" minFractionDigits="2"/> DH</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <input type="number" name="quantite" min="1" value="1" class="form-control" required>
        </div>
        <div class="col-md-3">
            <button type="submit" class="btn btn-primary">Ajouter</button>
        </div>
    </form>
</c:if>

<%-- Actions (le vrai controle des droits est fait par le service) --%>
<div class="d-flex gap-2">
    <c:if test="${commande.statut == 'PRETE' && role == 'SERVEUR'}">
        <form method="post" action="${ctx}/commandes/statut">
            <input type="hidden" name="idCommande" value="${commande.idCommande}">
            <input type="hidden" name="nouveau" value="SERVIE">
            <button type="submit" class="btn btn-success">Marquer servie</button>
        </form>
    </c:if>
    <c:if test="${commande.statut == 'SERVIE' && role == 'SERVEUR'}">
        <form method="post" action="${ctx}/commandes/encaisser">
            <input type="hidden" name="idCommande" value="${commande.idCommande}">
            <button type="submit" class="btn btn-success">Encaisser</button>
        </form>
    </c:if>
    <c:if test="${(commande.statut == 'EN_ATTENTE' && (role == 'SERVEUR' || role == 'ADMIN'))
                  || (commande.statut == 'EN_PREPARATION' && role == 'ADMIN')}">
        <form method="post" action="${ctx}/commandes/annuler">
            <input type="hidden" name="idCommande" value="${commande.idCommande}">
            <button type="submit" class="btn btn-outline-danger">Annuler la commande</button>
        </form>
    </c:if>
    <a href="${ctx}/commandes" class="btn btn-outline-secondary">Retour a la liste</a>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>