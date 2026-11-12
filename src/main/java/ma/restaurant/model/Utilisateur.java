package ma.restaurant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Une ligne de la table UTILISATEUR. */
@Entity
@Table(name = "UTILISATEUR")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUtilisateur")
    private Long idUtilisateur;

    @Column(name = "nomUtilisateur", nullable = false, unique = true, length = 50)
    private String nomUtilisateur;

    @Column(name = "motDePasseHash", nullable = false, length = 100)
    private String motDePasseHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "mdpTemporaire", nullable = false)
    private boolean mdpTemporaire = false;

    @Column(name = "tentativesEchec", nullable = false)
    private int tentativesEchec = 0;

    @Column(name = "verrouille", nullable = false)
    private boolean verrouille = false;

    /** Obligatoire pour JPA. */
    public Utilisateur() {}

    public Utilisateur(Long idUtilisateur, String nomUtilisateur, String motDePasseHash, Role role) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public boolean isMdpTemporaire() { return mdpTemporaire; }
    public void setMdpTemporaire(boolean mdpTemporaire) { this.mdpTemporaire = mdpTemporaire; }

    public int getTentativesEchec() { return tentativesEchec; }
    public void setTentativesEchec(int tentativesEchec) { this.tentativesEchec = tentativesEchec; }

    public boolean isVerrouille() { return verrouille; }
    public void setVerrouille(boolean verrouille) { this.verrouille = verrouille; }
}
