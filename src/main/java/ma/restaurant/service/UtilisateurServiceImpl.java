package ma.restaurant.service;

import java.util.List;
import java.util.Optional;

import ma.restaurant.dao.UtilisateurDao;
import ma.restaurant.exception.AuthentificationException;
import ma.restaurant.exception.CompteInactifException;
import ma.restaurant.exception.CompteVerrouilleException;
import ma.restaurant.exception.EntiteIntrouvableException;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.util.JPAUtil;
import ma.restaurant.util.PasswordUtil;

public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    public Utilisateur findById(Long id) {
        return JPAUtil.executeRead(em -> {
            Utilisateur utilisateur = utilisateurDao.findById(em, id);
            if (utilisateur == null) {
                throw new EntiteIntrouvableException("Utilisateur introuvable : " + id);
            }
            return utilisateur;
        });
    }

    @Override
    public List<Utilisateur> findAll() {
        return JPAUtil.executeRead(utilisateurDao::findAll);
    }

    @Override
    public Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur) {
        return JPAUtil.executeRead(em -> utilisateurDao.findByNomUtilisateur(em, nomUtilisateur));
    }

    @Override
    public Utilisateur authentifier(String nomUtilisateur, String motDePasse) {
        if (nomUtilisateur == null || nomUtilisateur.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            throw new AuthentificationException("Identifiant ou mot de passe incorrect.");
        }

        // 1. Recherche de l'utilisateur par nom
        Optional<Utilisateur> optUser = findByNomUtilisateur(nomUtilisateur);
        if (optUser.isEmpty()) {
            // Identifiant introuvable : message générique pour éviter l'énumération des utilisateurs
            throw new AuthentificationException("Identifiant ou mot de passe incorrect.");
        }

        Utilisateur user = optUser.get();

        // 2. Vérification si déjà verrouillé
        if (user.isVerrouille()) {
            throw new CompteVerrouilleException("Ce compte est verrouillé suite à trop d'échecs de connexion.");
        }

        // 3. Vérification si inactif
        if (!user.isActif()) {
            throw new CompteInactifException("Ce compte a été désactivé.");
        }

        // 4. Vérification du mot de passe avec BCrypt
        boolean mdpValide = PasswordUtil.verifier(motDePasse, user.getMotDePasseHash());

        if (!mdpValide) {
            // L'échec doit impérativement être persisté et COMMITTÉ en base.
            // On ne doit pas lancer l'exception dans la transaction, sinon JPAUtil fait un rollback !
            boolean verrouille = JPAUtil.executeInTransaction(em -> {
                Utilisateur u = utilisateurDao.findById(em, user.getIdUtilisateur());
                if (u != null) {
                    int nouvellesTentatives = u.getTentativesEchec() + 1;
                    u.setTentativesEchec(nouvellesTentatives);

                    if (nouvellesTentatives >= MAX_TENTATIVES_ECHEC) {
                        u.setVerrouille(true);
                        utilisateurDao.update(em, u);
                        return true;
                    } else {
                        utilisateurDao.update(em, u);
                        return false;
                    }
                }
                return false;
            });

            if (verrouille) {
                throw new CompteVerrouilleException(
                        "Compte verrouillé suite à " + MAX_TENTATIVES_ECHEC + " tentatives infructueuses.");
            } else {
                throw new AuthentificationException("Identifiant ou mot de passe incorrect.");
            }
        }

        // 5. Authentification réussie : réinitialisation du compteur d'échecs
        if (user.getTentativesEchec() > 0) {
            JPAUtil.executeInTransaction(em -> {
                Utilisateur u = utilisateurDao.findById(em, user.getIdUtilisateur());
                if (u != null) {
                    u.setTentativesEchec(0);
                    utilisateurDao.update(em, u);
                }
                return null;
            });
            user.setTentativesEchec(0);
        }

        return user;
    }

    @Override
    public void deverrouiller(Long id) {
        JPAUtil.executeInTransaction(em -> {
            Utilisateur user = utilisateurDao.findById(em, id);
            if (user == null) {
                throw new EntiteIntrouvableException("Utilisateur introuvable : " + id);
            }
            user.setVerrouille(false);
            user.setTentativesEchec(0);
            utilisateurDao.update(em, user);
            return null;
        });
    }
}
