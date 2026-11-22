package ma.restaurant.util;

import ma.restaurant.model.Role;

/** Ce qu'on garde en session apres la connexion (classe avec getters, lue par les JSP). */
public class SessionUser {
    private final Long id;
    private final String login;
    private final Role role;

    public SessionUser(Long id, String login, Role role) {
        this.id = id;
        this.login = login;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getLogin() { return login; }
    public Role getRole() { return role; }

    /** Retourne le nom textuel du role (ex: "ADMIN", "SERVEUR", "CUISINIER").
     *  Utilise par les JSP via ${sessionScope.user.roleNom} pour comparer avec des chaines EL. */
    public String getRoleNom() { return role != null ? role.name() : ""; }
}
