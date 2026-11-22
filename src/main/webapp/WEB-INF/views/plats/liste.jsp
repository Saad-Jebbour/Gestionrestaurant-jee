<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Gestion des plats"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="mb-1">Carte des plats</h2>
        <p class="text-muted mb-0">Consultez, recherchez et gérez les plats du restaurant.</p>
    </div>
    <a href="${ctx}/plats/nouveau" class="btn btn-primary">
        + Nouveau plat
    </a>
</div>

<!-- Formulaire de recherche et de filtres -->
<div class="card shadow-sm border-0 mb-4">
    <div class="card-body p-3">
        <form method="get" action="${ctx}/plats" class="row g-2 align-items-end">
            <!-- Recherche par mot-clé -->
            <div class="col-md-4">
                <label for="motCle" class="form-label small text-muted mb-1">Mot-clé</label>
                <input type="text"
                       id="motCle"
                       name="motCle"
                       class="form-control"
                       placeholder="Nom ou description..."
                       value="<c:out value='${motCle}'/>">
            </div>

            <!-- Filtre par disponibilité -->
            <div class="col-md-3">
                <label for="disponible" class="form-label small text-muted mb-1">Disponibilité</label>
                <select id="disponible" name="disponible" class="form-select">
                    <option value="" ${empty disponible ? 'selected' : ''}>Tous les statuts</option>
                    <option value="true" ${disponible == 'true' ? 'selected' : ''}>Disponible uniquement</option>
                    <option value="false" ${disponible == 'false' ? 'selected' : ''}>Indisponible uniquement</option>
                </select>
            </div>

            <!-- Filtre par prix min -->
            <div class="col-md-2">
                <label for="prixMin" class="form-label small text-muted mb-1">Prix min (DH)</label>
                <input type="number"
                       step="0.01"
                       min="0"
                       id="prixMin"
                       name="prixMin"
                       class="form-control"
                       placeholder="0.00"
                       value="<c:out value='${prixMin}'/>">
            </div>

            <!-- Filtre par prix max -->
            <div class="col-md-2">
                <label for="prixMax" class="form-label small text-muted mb-1">Prix max (DH)</label>
                <input type="number"
                       step="0.01"
                       min="0"
                       id="prixMax"
                       name="prixMax"
                       class="form-control"
                       placeholder="100.00"
                       value="<c:out value='${prixMax}'/>">
            </div>

            <!-- Boutons de filtrage -->
            <div class="col-md-1 d-flex gap-1">
                <button type="submit" class="btn btn-primary w-100" title="Rechercher">
                    Filtrer
                </button>
            </div>
        </form>

        <c:if test="${not empty motCle or not empty disponible or not empty prixMin or not empty prixMax}">
            <div class="mt-2 pt-2 border-top d-flex justify-content-between align-items-center">
                <small class="text-muted">Filtres actifs appliqués.</small>
                <a href="${ctx}/plats" class="btn btn-sm btn-link text-decoration-none p-0 text-danger">
                    &times; Réinitialiser les filtres
                </a>
            </div>
        </c:if>
    </div>
</div>

<c:choose>
    <c:when test="${empty plats}">
        <div class="alert alert-info text-center py-4">
            <p class="mb-2">Aucun plat ne correspond à vos critères de recherche.</p>
            <c:if test="${not empty motCle or not empty disponible or not empty prixMin or not empty prixMax}">
                <a href="${ctx}/plats" class="btn btn-sm btn-outline-primary">Effacer les filtres</a>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card shadow-sm border-0 mb-3">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width: 80px;">#ID</th>
                            <th scope="col">Nom</th>
                            <th scope="col">Description</th>
                            <th scope="col" style="width: 130px;">Prix</th>
                            <th scope="col" style="width: 140px;">Disponibilité</th>
                            <th scope="col" class="text-end" style="width: 260px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${plats}">
                            <tr>
                                <td><strong><c:out value="${p.idPlat}"/></strong></td>
                                <td>
                                    <strong><c:out value="${p.nom}"/></strong>
                                </td>
                                <td>
                                    <span class="text-muted">
                                        <c:out value="${empty p.description ? '-' : p.description}"/>
                                    </span>
                                </td>
                                <td>
                                    <span class="fw-bold"><c:out value="${p.prix}"/> DH</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.disponible}">
                                            <span class="badge bg-success">Disponible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">Indisponible</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <div class="d-inline-flex gap-1">
                                        <!-- Bascule de disponibilité rapide -->
                                        <form method="post" action="${ctx}/plats/basculer-disponibilite" class="d-inline">
                                            <input type="hidden" name="id" value="${p.idPlat}">
                                            <button type="submit"
                                                    class="btn btn-sm ${p.disponible ? 'btn-outline-warning' : 'btn-outline-success'}"
                                                    title="${p.disponible ? 'Désactiver' : 'Activer'}">
                                                <c:out value="${p.disponible ? 'Rendre indispo.' : 'Rendre dispo.'}"/>
                                            </button>
                                        </form>

                                        <!-- Modifier -->
                                        <a href="${ctx}/plats/modifier?id=${p.idPlat}"
                                           class="btn btn-sm btn-outline-primary">
                                            Modifier
                                        </a>

                                        <!-- Supprimer -->
                                        <form method="post" action="${ctx}/plats/supprimer"
                                              class="d-inline"
                                              onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer le plat « <c:out value="${p.nom}"/> » ?');">
                                            <input type="hidden" name="id" value="${p.idPlat}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                Supprimer
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Pagination et résumé -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-center gap-3">
            <span class="text-muted small">
                Affichage de <strong>${plats.size()}</strong> sur <strong>${totalElements}</strong> plat(s) au total (Page ${pageCourante} sur ${totalPages})
            </span>

            <c:if test="${totalPages > 1}">
                <nav aria-label="Pagination des plats">
                    <ul class="pagination pagination-sm mb-0">
                        <%-- Précédent --%>
                        <li class="page-item ${pageCourante <= 1 ? 'disabled' : ''}">
                            <c:url var="urlPrec" value="/plats">
                                <c:if test="${not empty motCle}"><c:param name="motCle" value="${motCle}"/></c:if>
                                <c:if test="${not empty disponible}"><c:param name="disponible" value="${disponible}"/></c:if>
                                <c:if test="${not empty prixMin}"><c:param name="prixMin" value="${prixMin}"/></c:if>
                                <c:if test="${not empty prixMax}"><c:param name="prixMax" value="${prixMax}"/></c:if>
                                <c:param name="page" value="${pageCourante - 1}"/>
                            </c:url>
                            <a class="page-link" href="${urlPrec}">Précédent</a>
                        </li>

                        <%-- Numéros de pages --%>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:url var="urlPage" value="/plats">
                                <c:if test="${not empty motCle}"><c:param name="motCle" value="${motCle}"/></c:if>
                                <c:if test="${not empty disponible}"><c:param name="disponible" value="${disponible}"/></c:if>
                                <c:if test="${not empty prixMin}"><c:param name="prixMin" value="${prixMin}"/></c:if>
                                <c:if test="${not empty prixMax}"><c:param name="prixMax" value="${prixMax}"/></c:if>
                                <c:param name="page" value="${i}"/>
                            </c:url>
                            <li class="page-item ${i == pageCourante ? 'active' : ''}">
                                <a class="page-link" href="${urlPage}">${i}</a>
                            </li>
                        </c:forEach>

                        <%-- Suivant --%>
                        <li class="page-item ${pageCourante >= totalPages ? 'disabled' : ''}">
                            <c:url var="urlSuiv" value="/plats">
                                <c:if test="${not empty motCle}"><c:param name="motCle" value="${motCle}"/></c:if>
                                <c:if test="${not empty disponible}"><c:param name="disponible" value="${disponible}"/></c:if>
                                <c:if test="${not empty prixMin}"><c:param name="prixMin" value="${prixMin}"/></c:if>
                                <c:if test="${not empty prixMax}"><c:param name="prixMax" value="${prixMax}"/></c:if>
                                <c:param name="page" value="${pageCourante + 1}"/>
                            </c:url>
                            <a class="page-link" href="${urlSuiv}">Suivant</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
