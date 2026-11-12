package ma.restaurant.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * Point d'entree unique vers la base de donnees.
 * Lit l'URL, l'utilisateur et le mot de passe MySQL dans application.properties
 * (fichier local, ignore par Git).
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT = "RestaurantPU";
    private static final String FICHIER_CONFIG = "application.properties";

    private static EntityManagerFactory emf;

    private JPAUtil() {}

    private static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, chargerConfiguration());
        }
        return emf;
    }

    private static Map<String, String> chargerConfiguration() {
        Properties props = new Properties();
        try (InputStream in = JPAUtil.class.getClassLoader().getResourceAsStream(FICHIER_CONFIG)) {
            if (in == null) {
                throw new IllegalStateException(FICHIER_CONFIG + " introuvable dans src/main/resources. "
                        + "Copiez application.properties.example en application.properties "
                        + "et renseignez votre mot de passe MySQL.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible de " + FICHIER_CONFIG, e);
        }
        Map<String, String> config = new HashMap<>();
        config.put("jakarta.persistence.jdbc.url", exiger(props, "db.url"));
        config.put("jakarta.persistence.jdbc.user", exiger(props, "db.user"));
        config.put("jakarta.persistence.jdbc.password", exiger(props, "db.password"));
        return config;
    }

    private static String exiger(Properties props, String cle) {
        String valeur = props.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalStateException("Propriete manquante dans " + FICHIER_CONFIG + " : " + cle);
        }
        return valeur.trim();
    }

    /** Ouvre un EntityManager. A fermer par l'appelant (em.close()). */
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /** Ferme la connexion a la base (a appeler a l'arret de l'application). */
    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /** Lecture seule : ouvre un EntityManager, execute le travail, le ferme. */
    public static <T> T executeRead(Function<EntityManager, T> travail) {
        EntityManager em = createEntityManager();
        try {
            return travail.apply(em);
        } finally {
            em.close();
        }
    }

    /** Ecriture : tout ou rien. En cas d'erreur, la transaction est annulee (rollback). */
    public static <T> T executeInTransaction(Function<EntityManager, T> travail) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultat = travail.apply(em);
            tx.commit();
            return resultat;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
