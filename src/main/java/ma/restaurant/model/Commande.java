package ma.restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Une ligne de la table COMMANDE : le "ticket" d'une table. */
@Entity
@Table(name = "COMMANDE")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCommande")
    private Long idCommande;

    @Column(name = "dateCommande", nullable = false)
    private LocalDateTime dateCommande;

    @Column(name = "numeroTable", nullable = false)
    private int numeroTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /** Le serveur qui a cree la commande (relation "cree" du diagramme). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    /** Les lignes du ticket. Si on supprime la commande, ses lignes partent avec elle. */
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailCommande> details = new ArrayList<>();

    /** Obligatoire pour JPA. */
    public Commande() {}

    // ----- Relation avec les lignes (garde les 2 cotes coherents) -----

    public void ajouterDetail(DetailCommande detail) {
        details.add(detail);
        detail.setCommande(this);
    }

    public void retirerDetail(Long idDetail) {
        details.removeIf(d -> idDetail != null && idDetail.equals(d.getIdDetail()));
    }

    // ----- Calcul du total : etape 10 -----

    public BigDecimal calculerTotal() {
        throw new UnsupportedOperationException("A implementer a l'etape 10");
    }

    // ----- Getters / setters -----

    public Long getIdCommande() { return idCommande; }
    public void setIdCommande(Long idCommande) { this.idCommande = idCommande; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public int getNumeroTable() { return numeroTable; }
    public void setNumeroTable(int numeroTable) { this.numeroTable = numeroTable; }

    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public List<DetailCommande> getDetails() { return details; }
    public void setDetails(List<DetailCommande> details) { this.details = details; }
}
