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
 * Initialiseur automatique de la base de données au démarrage de l'application :
 * - Déclenche la création automatique des tables par Hibernate (hbm2ddl.auto = update).
 * - Initialise les comptes par défaut si la table UTILISATEUR est vide (sans script SQL externe).
 * - Initialise des plats d'exemple si la carte est vide.
 * - Ferme proprement l'EntityManagerFactory à l'arrêt du serveur Tomcat.
 */
@WebListener
public class DatabaseInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DatabaseInitializer] Initialisation JPA et vérification du schéma...");

        try {
            JPAUtil.executeInTransaction(em -> {
                // 1. Initialiser les comptes de test si aucun utilisateur n'existe
                Long countUsers = em.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class)
                        .getSingleResult();

                if (countUsers == 0) {
                    System.out.println("[DatabaseInitializer] Table UTILISATEUR vide : création des comptes par défaut...");

                    Utilisateur admin = new Utilisateur(null, "admin", PasswordUtil.hacher("Admin@2026"), Role.ADMIN);
                    Utilisateur serveur = new Utilisateur(null, "serveur1", PasswordUtil.hacher("Serveur@2026"), Role.SERVEUR);
                    Utilisateur cuisinier = new Utilisateur(null, "cuisto1", PasswordUtil.hacher("Cuisto@2026"), Role.CUISINIER);

                    em.persist(admin);
                    em.persist(serveur);
                    em.persist(cuisinier);
                    System.out.println("[DatabaseInitializer] Comptes admin, serveur1 et cuisto1 créés avec succès.");
                }

                // 2. Initialiser des plats par défaut si la table PLAT est vide
                Long countPlats = em.createQuery("SELECT COUNT(p) FROM Plat p", Long.class)
                        .getSingleResult();

                if (countPlats == 0) {
                    System.out.println("[DatabaseInitializer] Table PLAT vide : création de la carte initiale...");

                    em.persist(new Plat(null, "Couscous Royal", "Semoule fine, sept légumes de saison, agneau, poulet et merguez.", new BigDecimal("95.00"), true));
                    em.persist(new Plat(null, "Tajine de Poulet au Citron", "Poulet fermier mariné aux épices, citrons confits et olives violettes.", new BigDecimal("75.00"), true));
                    em.persist(new Plat(null, "Tajine de Veau aux Pruneaux", "Veau tendre mijoté, pruneaux caramélisés, amandes grillées et graines de sésame.", new BigDecimal("85.00"), true));
                    em.persist(new Plat(null, "Pastilla au Poulet", "Feuilleté croustillant au poulet effiloché, amandes concassées, cannelle et sucre glace.", new BigDecimal("70.00"), true));
                    em.persist(new Plat(null, "Salade Marocaine Traditionnelle", "Tomates fraîches, poivrons grillés, concombres, oignons rouges et coriandre.", new BigDecimal("35.00"), true));
                    em.persist(new Plat(null, "Zaalouk d'Aubergines", "Caviar d'aubergines rôties, tomates compotées à l'huile d'olive, ail et cumin.", new BigDecimal("40.00"), true));
                    em.persist(new Plat(null, "Briouates aux Amandes", "Triangles dorés au miel d'oranger et pâte d'amandes parfumée.", new BigDecimal("45.00"), true));
                    em.persist(new Plat(null, "Thé à la Menthe Fraîche", "Thé vert gunpowder infusé à la menthe fraîche parfumée.", new BigDecimal("20.00"), true));

                    System.out.println("[DatabaseInitializer] 8 plats initiaux ajoutés avec succès.");
                }

                return null;
            });

            System.out.println("[DatabaseInitializer] Base de données prête et initialisée avec succès !");

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
