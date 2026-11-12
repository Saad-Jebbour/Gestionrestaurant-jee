package ma.restaurant.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Une ligne de la table PLAT. */
@Entity
@Table(name = "PLAT")
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPlat")
    private Long idPlat;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "prix", nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    /** Obligatoire pour JPA. */
    public Plat() {}

    public Plat(Long idPlat, String nom, String description, BigDecimal prix, boolean disponible) {
        this.idPlat = idPlat;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.disponible = disponible;
    }

    public Long getIdPlat() { return idPlat; }
    public void setIdPlat(Long idPlat) { this.idPlat = idPlat; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
