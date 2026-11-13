# Contrats

## Statuts de commande
EN_ATTENTE, EN_PREPARATION, PRETE, SERVIE, PAYEE, ANNULEE

## Transitions autorisées

| De \ Vers        | Qui peut le faire        |
|------------------|--------------------------|
| EN_ATTENTE → EN_PREPARATION | Cuisinier |
| EN_ATTENTE → ANNULEE        | Serveur, Admin |
| EN_PREPARATION → PRETE      | Cuisinier |
| EN_PREPARATION → ANNULEE    | Admin |
| PRETE → SERVIE              | Serveur |
| SERVIE → PAYEE              | Serveur (encaisser) |

PAYEE et ANNULEE sont des statuts finaux : plus aucune transition.
Toute autre transition lance TransitionInterditeException.

## Modification d'une commande
Ajouter ou retirer un plat n'est possible que si le statut est EN_ATTENTE.
Sinon : CommandeNonModifiableException.

## Prix figé
DetailCommande.prixUnitaire est copié depuis Plat.prix au moment de l'ajout.