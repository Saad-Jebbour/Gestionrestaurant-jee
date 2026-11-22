<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="${edition ? 'Modifier un plat' : 'Nouveau plat'}"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white py-3">
                <h4 class="card-title mb-0">
                    <c:out value="${edition ? 'Modifier le plat' : 'Ajouter un nouveau plat'}"/>
                </h4>
            </div>
            <div class="card-body p-4">
                <form method="post" action="${ctx}/plats/enregistrer">
                    <c:if test="${edition}">
                        <input type="hidden" name="idPlat" value="<c:out value='${plat.idPlat}'/>">
                    </c:if>

                    <!-- Nom du plat -->
                    <div class="mb-3">
                        <label for="nom" class="form-label fw-bold">Nom du plat <span class="text-danger">*</span></label>
                        <input type="text"
                               class="form-control"
                               id="nom"
                               name="nom"
                               value="<c:out value='${plat.nom}'/>"
                               placeholder="ex: Couscous Royal, Tajine de poulet..."
                               required
                               autofocus>
                    </div>

                    <!-- Prix -->
                    <div class="mb-3">
                        <label for="prix" class="form-label fw-bold">Prix (DH) <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="number"
                                   class="form-control"
                                   id="prix"
                                   name="prix"
                                   step="0.01"
                                   min="0.01"
                                   value="<c:out value='${plat.prix}'/>"
                                   placeholder="ex: 85.00"
                                   required>
                            <span class="input-group-text">DH</span>
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label fw-bold">Description</label>
                        <textarea class="form-control"
                                  id="description"
                                  name="description"
                                  rows="3"
                                  placeholder="Ingrédients, présentation, allergènes..."><c:out value="${plat.description}"/></textarea>
                    </div>

                    <!-- Disponibilité -->
                    <div class="mb-4 form-check form-switch">
                        <input class="form-check-input"
                               type="checkbox"
                               role="switch"
                               id="disponible"
                               name="disponible"
                               value="true"
                               ${plat.disponible ? 'checked' : ''}>
                        <label class="form-check-label" for="disponible">
                            Disponible à la commande immédiatement
                        </label>
                    </div>

                    <!-- Boutons d'action -->
                    <div class="d-flex justify-content-between align-items-center">
                        <a href="${ctx}/plats" class="btn btn-outline-secondary">
                            Annuler
                        </a>
                        <button type="submit" class="btn btn-primary px-4">
                            <c:out value="${edition ? 'Enregistrer les modifications' : 'Créer le plat'}"/>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
