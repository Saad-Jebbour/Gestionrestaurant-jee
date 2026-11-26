# Gestion des commandes d'un restaurant

Application web J2EE de gestion des commandes d'un restaurant (projet du module J2EE, ENSA Agadir).
Trois rôles : **Administrateur**, **Serveur**, **Cuisinier**.

## Fonctionnalités

- Authentification (mots de passe hachés avec BCrypt, verrouillage après 5 échecs)
- Gestion des utilisateurs et des plats (CRUD, recherche, pagination) : Administrateur
- Commandes : création, ajout/retrait de plats, liste avec filtres (date, statut, table), détail
- Cycle de vie d'une commande : `EN_ATTENTE` → `EN_PREPARATION` → `PRETE` → `SERVIE` → `PAYEE` (ou `ANNULEE`)
- Écran de la cuisine pour le Cuisinier
- Statistiques : chiffre d'affaires par jour, plats les plus commandés
- Pages d'erreur 401 et 403

## Règles de gestion

- Le total d'une commande est la somme des sous-totaux de ses lignes (jamais saisi à la main)
- Le prix d'un plat est figé dans la ligne de commande au moment de l'ajout
- Ajouter un plat déjà présent incrémente la quantité de la ligne
- Un plat indisponible ne peut pas être commandé
- Les transitions de statut sont réservées à certains rôles (voir `docs/contrats.md`)
- Une commande n'est modifiable que lorsqu'elle est `EN_ATTENTE`

## Technologies

Java 17, Jakarta EE 10 (Servlet 6, JSP/JSTL), Hibernate 6 / JPA 3.1, MySQL 8,
Tomcat 10.1, Maven, JUnit 5, Mockito, Bootstrap 5, Git / GitHub.

## Prérequis

JDK 17, Maven, Tomcat 10.1, MySQL 8 démarré.

## Installation

```bash
git clone [URL-DU-DEPOT]
cd Gestionrestaurant-jee
```

1. Copier `application.properties.example` en `application.properties` et y renseigner
   l'URL de la base, l'utilisateur et le mot de passe MySQL.
   **Ce fichier ne doit jamais être commité** (il est ignoré par Git).
2. Construire le projet :

```bash
mvn package
```

3. Déployer `target/restaurant.war` sur Tomcat 10.1 (ou lancer depuis IntelliJ avec Smart Tomcat,
   *context path* `/restaurant`).
4. Ouvrir <http://localhost:8080/restaurant/>.

Au premier démarrage, Hibernate crée les tables et les données de départ (comptes de test, plats)
sont insérées si la base est vide.

## Comptes de test

| Rôle | Identifiant | Mot de passe |
|---|---|---|
| Administrateur | [à compléter] | [à compléter] |
| Serveur | [à compléter] | [à compléter] |
| Cuisinier | [à compléter] | [à compléter] |

## Structure du projet

```
src/main/java/ma/restaurant/
├── model/       entités JPA et énumérations (Role, StatutCommande)
├── dao/         accès aux données (JPQL)
├── service/     règles métier
├── exception/   exceptions métier
├── servlet/     contrôleurs
├── filter/      authentification et autorisation
├── listener/    initialisation de la base
└── util/        JPAUtil, SessionUser
src/main/webapp/WEB-INF/views/   pages JSP
docs/                            contrats, diagrammes UML
```

## Tests

```bash
mvn test
```

## Organisation du travail

Branches `main` (stable), `develop` (intégration) et `feature/*`. Aucun merge sans Pull Request
relue par l'autre membre. Messages de commit : `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`.
Voir [CONTRIBUTING.md](CONTRIBUTING.md).

## Auteurs

- Saad Jebbour : utilisateurs, sécurité, plats, base de données
- Noora [NOM] : commandes, UML, statistiques, rapport

Encadrant : [nom]. Année universitaire 2026-2027.