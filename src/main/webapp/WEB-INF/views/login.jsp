<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Connexion - Gestion restaurant"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="row justify-content-center mt-4">
    <div class="col-md-6 col-lg-4">
        <div class="card shadow-sm border-0">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-4 text-primary">Connexion</h3>

                <c:if test="${not empty erreur}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${erreur}"/>
                    </div>
                </c:if>

                <form method="post" action="${ctx}/login" novalidate>
                    <div class="mb-3">
                        <label for="nomUtilisateur" class="form-label">Nom d'utilisateur</label>
                        <input type="text"
                               class="form-control"
                               id="nomUtilisateur"
                               name="nomUtilisateur"
                               value="<c:out value='${nomUtilisateur}'/>"
                               placeholder="ex: admin, serveur1..."
                               required
                               autofocus>
                    </div>

                    <div class="mb-3">
                        <label for="motDePasse" class="form-label">Mot de passe</label>
                        <input type="password"
                               class="form-control"
                               id="motDePasse"
                               name="motDePasse"
                               placeholder="Votre mot de passe"
                               required>
                    </div>

                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-primary btn-lg">Se connecter</button>
                    </div>
                </form>
            </div>
            <div class="card-footer bg-light text-muted small p-3 text-center">
                <span>Comptes de test : <strong>admin</strong> / <strong>serveur1</strong> / <strong>cuisto1</strong></span>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
