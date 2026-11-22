<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Gestion des plats"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="mb-1">Carte des plats</h2>
        <p class="text-muted mb-0">Consultez, ajoutez et gérez la disponibilité des plats du restaurant.</p>
    </div>
    <a href="${ctx}/plats/nouveau" class="btn btn-primary">
        + Nouveau plat
    </a>
</div>

<c:choose>
    <c:when test="${empty plats}">
        <div class="alert alert-info text-center py-4">
            <p class="mb-2">Aucun plat n'a été créé pour le moment.</p>
            <a href="${ctx}/plats/nouveau" class="btn btn-sm btn-primary">Créer le premier plat</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width: 80px;">#ID</th>
                            <th scope="col">Nom</th>
                            <th scope="col">Description</th>
                            <th scope="col" style="width: 120px;">Prix</th>
                            <th scope="col" style="width: 140px;">Disponibilité</th>
                            <th scope="col" class="text-end" style="width: 240px;">Actions</th>
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
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
