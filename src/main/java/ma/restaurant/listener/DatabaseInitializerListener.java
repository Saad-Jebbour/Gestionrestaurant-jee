package ma.restaurant.listener;

import java.math.BigDecimal;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ma.restaurant.model.Plat;
import ma.restaurant.model.Role;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.util.JPAUtil;
import ma.restaurant.util.PasswordUtil;

/**
 * Initialiseur automatique de la base de donnees au demarrage de l'application :
 * - Declenche la creation automatique des tables par Hibernate (hbm2ddl.auto = update).
 * - Initialise les comptes par defaut si la table UTILISATEUR est vide.
 * - S'assure que tous les comptes sont actifs et deverrouilles (correction du defaut MySQL a 0).
 * - Initialise des plats d'exemple si la carte est vide.
 * - Ferme proprement l'EntityManagerFactory a l'arret du serveur Tomcat.
 */
@WebListener
public class DatabaseInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DatabaseInitializer] Initialisation JPA et verification du schema...");

        try {
            JPAUtil.executeInTransaction(em -> {

                // 0. Migration automatique des colonnes existantes si crees avec un ancien ENUM MySQL
                try {
                    em.createNativeQuery("ALTER TABLE COMMANDE MODIFY COLUMN statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE'")
                            .executeUpdate();
                } catch (Exception ignored) {
                    // Ignore si la table n'existe pas encore ou la colonne est deja compatible
                }

                try {
                    em.createNativeQuery("ALTER TABLE UTILISATEUR MODIFY COLUMN role VARCHAR(30) NOT NULL")
                            .executeUpdate();
                } catch (Exception ignored) {
                }

                // 1. Initialiser les comptes de test si aucun utilisateur n'existe
                Long countUsers = em.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class)
                        .getSingleResult();

                if (countUsers == 0) {
                    System.out.println("[DatabaseInitializer] Table UTILISATEUR vide : creation des comptes par defaut...");

                    Utilisateur admin = new Utilisateur(null, "admin", PasswordUtil.hacher("Admin@2026"), Role.ADMIN);
                    Utilisateur serveur = new Utilisateur(null, "serveur1", PasswordUtil.hacher("Serveur@2026"), Role.SERVEUR);
                    Utilisateur cuisinier = new Utilisateur(null, "cuisto1", PasswordUtil.hacher("Cuisto@2026"), Role.CUISINIER);

                    em.persist(admin);
                    em.persist(serveur);
                    em.persist(cuisinier);
                    System.out.println("[DatabaseInitializer] Comptes admin, serveur1 et cuisto1 crees avec succes.");
                }

                // S'assurer que tous les comptes existants sont bien actifs
                // (au cas ou Hibernate a ajoute la colonne actif avec la valeur 0 par defaut)
                int misAJour = em.createQuery(
                        "UPDATE Utilisateur u SET u.actif = true WHERE u.actif = false")
                        .executeUpdate();
                if (misAJour > 0) {
                    System.out.println("[DatabaseInitializer] " + misAJour + " compte(s) reactive(s) avec succes.");
                }

                // 2. Initialiser des plats par defaut si la table PLAT est vide
                Long countPlats = em.createQuery("SELECT COUNT(p) FROM Plat p", Long.class)
                        .getSingleResult();

                if (countPlats == 0) {
                    System.out.println("[DatabaseInitializer] Table PLAT vide : creation de la carte initiale...");

                    em.persist(new Plat(null, "Couscous Royal",
                            "Semoule fine, sept legumes de saison, agneau, poulet et merguez.",
                            new BigDecimal("95.00"), true));
                    em.persist(new Plat(null, "Tajine de Poulet au Citron",
                            "Poulet fermier marine aux epices, citrons confits et olives violettes.",
                            new BigDecimal("75.00"), true));
                    em.persist(new Plat(null, "Tajine de Veau aux Pruneaux",
                            "Veau tendre mijote, pruneaux caramelises, amandes grillees et graines de sesame.",
                            new BigDecimal("85.00"), true));
                    em.persist(new Plat(null, "Pastilla au Poulet",
                            "Feuillete croustillant au poulet effiloche, amandes concassees, cannelle et sucre glace.",
                            new BigDecimal("70.00"), true));
                    em.persist(new Plat(null, "Salade Marocaine Traditionnelle",
                            "Tomates fraiches, poivrons grilles, concombres, oignons rouges et coriandre.",
                            new BigDecimal("35.00"), true));
                    em.persist(new Plat(null, "Zaalouk d'Aubergines",
                            "Caviar d'aubergines roties, tomates compotees a l'huile d'olive, ail et cumin.",
                            new BigDecimal("40.00"), true));
                    em.persist(new Plat(null, "Briouates aux Amandes",
                            "Triangles dores au miel d'oranger et pate d'amandes parfumee.",
                            new BigDecimal("45.00"), true));
                    em.persist(new Plat(null, "The a la Menthe Fraiche",
                            "The vert gunpowder infuse a la menthe fraiche parfumee.",
                            new BigDecimal("20.00"), true));

                    System.out.println("[DatabaseInitializer] 8 plats initiaux ajoutes avec succes.");
                }

                return null;
            });

            System.out.println("[DatabaseInitializer] Base de donnees prete et initialisee avec succes !");

        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[DatabaseInitializer] Fermeture de l'EntityManagerFactory...");
        JPAUtil.close();
    }
}
