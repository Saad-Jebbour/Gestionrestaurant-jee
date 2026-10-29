CREATE DATABASE IF NOT EXISTS gestion_restaurant
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestion_restaurant;
SHOW DATABASES;
CREATE TABLE UTILISATEUR (
    idUtilisateur BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomUtilisateur VARCHAR(50) NOT NULL UNIQUE,
    motDePasseHash VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'SERVEUR', 'CUISINIER') NOT NULL
);
-- DESCRIBE UTILISATEUR;
CREATE TABLE PLAT (
    idPlat BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    prix DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_plat_prix
        CHECK (prix >= 0)
);
CREATE TABLE COMMANDE (
    idCommande BIGINT PRIMARY KEY AUTO_INCREMENT,
    dateCommande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    statut ENUM(
        'EN_ATTENTE',
        'EN_PREPARATION',
        'PRETE',
        'TERMINEE',
        'ANNULEE'
    ) NOT NULL DEFAULT 'EN_ATTENTE',

    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    idUtilisateur BIGINT NOT NULL,

    CONSTRAINT chk_commande_total
        CHECK (total >= 0),

    CONSTRAINT fk_commande_utilisateur
        FOREIGN KEY (idUtilisateur)
        REFERENCES UTILISATEUR(idUtilisateur)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
CREATE TABLE DETAIL_COMMANDE (
    idDetail BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantite INT NOT NULL,
    prixUnitaire DECIMAL(10,2) NOT NULL,
    sousTotal DECIMAL(10,2) NOT NULL,

    idCommande BIGINT NOT NULL,
    idPlat BIGINT NOT NULL,

    CONSTRAINT chk_detail_quantite
        CHECK (quantite > 0),

    CONSTRAINT chk_detail_prix
        CHECK (prixUnitaire >= 0),

    CONSTRAINT chk_detail_sous_total
        CHECK (sousTotal >= 0),

    CONSTRAINT uq_commande_plat
        UNIQUE (idCommande, idPlat),

    CONSTRAINT fk_detail_commande
        FOREIGN KEY (idCommande)
        REFERENCES COMMANDE(idCommande)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_detail_plat
        FOREIGN KEY (idPlat)
        REFERENCES PLAT(idPlat)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
-- DESCRIBE DETAIL_COMMANDE;
SHOW TABLES;
DESCRIBE PLAT;
DESCRIBE COMMANDE;
DESCRIBE DETAIL_COMMANDE;
