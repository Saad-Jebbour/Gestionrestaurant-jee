<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Nouvelle commande"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<h2>Nouvelle commande</h2>
<form method="post" action="${pageContext.request.contextPath}/commandes/creer" class="row g-3 mt-2">
    <div class="col-auto">
        <label for="numeroTable" class="form-label">Numero de table</label>
        <input type="number" min="1" required class="form-control" id="numeroTable" name="numeroTable">
    </div>
    <div class="col-auto align-self-end">
        <button type="submit" class="btn btn-primary">Creer la commande</button>
    </div>
</form>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>