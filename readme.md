Module de Validation Hebdomadaire (Suivi de Temps)
===========
Ce projet implémente un nouveau module destiné aux Managers pour la validation du suivi de temps hebdomadaire de leurs
équipes.

### 📜 Contexte Métier

L'objectif de l'application est de collecter, valider et permettre l'approbation des activités hebdomadaires des
techniciens, incluant le planning de base, les absences, les astreintes et les éléments variables de paie (EVP).

#### 🎯 _Ici, nous implémenterons uniquement la partie collecte et validation des données hebdomadaires._

### ✨ Fonctionnalités Clés

- Réception des données hebdomadaires : Intégration des plannings, absences, et sorties d'astreinte (du dimanche au
  samedi).

- Gestion des Éléments Variables (EVP) : Suivi des demandes de primes (panier repas, transport, incommodité, salissure).

- Système de Statuts : Les semaines sont suivies via des statuts (En attente, Approuvée, À contrôler) qui évoluent selon
  les actions du manager et les nouvelles réceptions de données.

- Calcul Automatisé : Le système calcule le temps de travail total en vérifiant la cohérence et le non-chevauchement des
  plages horaires.

### 👨‍💻 Rôles

- Technicien : (Implicite) Génère les données de suivi de temps.

- Manager : Valide, modifie et approuve les données hebdomadaires de son équipe. La validation se fait typiquement le
  lundi matin.

### 📋 Règles de Gestion Métier

Voici les règles de gestion (logique métier) identifiées pour le fonctionnement du module :

#### **1. Période et Définitions**

- La semaine de travail est définie du dimanche au samedi.
- Les astreintes concernent les horaires effectués en dehors de l'emploi du temps (planning de base).
- Le processus de validation managériale est prévu (cible) le lundi matin.

#### **2. Validation et Calcul (Soumission Initiale)**

- Non-chevauchement :
    - Les plages horaires du planning de base et des astreintes ne doivent pas se chevaucher entre elles.
    - Les plages horaires des absences et des astreintes ne doivent pas se chevaucher entre elles.
- Un EVP doit avoir un type et une date non null
- Rejet automatique : Si une incohérence est détectée lors de la réception des données, la soumission est rejetée.
- Calcul du temps travaillé : Temps travaillé = (Somme des Plannings de base + Somme des Sorties d'astreinte) - Somme
  des Absences (en respectant le non-chevauchement).

#### **3. Statuts et Mises à Jour**

Une semaine possède un statut : "En attente", "Approuvée", ou "À contrôler".

- Cas 1 (Nouvelle soumission sur "En attente") : Si de nouvelles données sont reçues pour une semaine au statut "En
  attente", les anciennes données sont écrasées et le statut reste "En attente".

- Cas 2 (Nouvelle soumission sur "Approuvée") : Si de nouvelles données sont reçues pour une semaine déjà "Approuvée" (
  par un manager), les anciennes données sont écrasées et le statut bascule à "À contrôler" (nécessite une nouvelle
  validation managériale).

Objet du kata :
=

Refactorer le code existant pour améliorer sa lisibilité, sa maintenabilité et son extensibilité, tout en conservant la
logique métier décrite ci-dessus. La cible est d'avoir une architecture hexagonale claire.

À noter : on peut changer le contrat d'interface si besoin, car on estime être maître du front et du back en même temps.
