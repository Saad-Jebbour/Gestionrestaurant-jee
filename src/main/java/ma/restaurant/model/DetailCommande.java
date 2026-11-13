package ma.restaurant.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Une ligne de la table DETAIL_COMMANDE : par exemple "2 pizzas a 45 DH = 90 DH". */
@Entity
@Table(name = "DETAIL_COMMANDE")
public class DetailCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDetail")
    private Long idDetail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCommande", nullable = false)
    private Commande commande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idPlat", nullable = false)
    private Plat plat;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    /** Prix fige : copie du prix du plat au moment de l'ajout (etape 10). */
    @Column(name = "prixUnitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "sousTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal;

    /** Obligatoire pour JPA. */
    public DetailCommande() {}

    // ----- Calculs : etape 10 -----

    public BigDecimal calculerSousTotal() {
        throw new UnsupportedOperationException("A implementer a l'etape 10");
    }

    public void mettreAJourQuantite(int qte) {
        throw new UnsupportedOperationException("A implementer a l'etape 10");
    }

    // ----- Getters / setters -----

    public Long getIdDetail() { return idDetail; }
    public void setIdDetail(Long idDetail) { this.idDetail = idDetail; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Plat getPlat() { return plat; }
    public void setPlat(Plat plat) { this.plat = plat; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public BigDecimal getSousTotal() { return sousTotal; }
    public void setSousTotal(BigDecimal sousTotal) { this.sousTotal = sousTotal; }
}