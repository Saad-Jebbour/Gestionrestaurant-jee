USE gestion_restaurant;

-- ========================================================
-- Comptes de test (Mots de passe conformes à la politique) :
-- admin    : Admin@2026
-- serveur1 : Serveur@2026
-- cuisto1  : Cuisto@2026
-- ========================================================

INSERT INTO UTILISATEUR (nomUtilisateur, motDePasseHash, role) VALUES
 ('admin',    '$2a$10$1DXlt5zEEkYJ0yK6cuuGP.DZGOVe7jrVhJuHY6my953663ZBSYNim', 'ADMIN'),
 ('serveur1', '$2a$10$2aV/B67aeINS9WKTR.wcFuKLrx7ZU5KZ7DHTTCpQICkYklHbCCK96', 'SERVEUR'),
 ('cuisto1',  '$2a$10$/4SzWApcSGQ/ANRUF6kx.eiTm8EGeqH4WuLD4y3zVGnBBei2RKkG2', 'CUISINIER');

