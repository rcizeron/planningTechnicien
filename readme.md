technicien
manager

nouveau module : validation hebdomadaire suivi de temps
par exemple astreinte, absence, disponibilité suivant emploi du temps
l'astreinte sont les horaires hors emploie du temps
validation le lundi matin

élément variable
il y a des primes
panier repas
incommodité
salissure
transport

travaux programmés
et heures excédentaires

------------------
technicien
manager

la semaine est du dimanche au samedi
c'est le manager qui valide ou pas
les statuts de la semaine :
en attente
approuvée
à controller

réception de données hebdomadaire pour un technicien
validation par le manager
modification par le manager

données hebdo
une semaine du dimanche au samedi
planning de base
List<Objet<date heure début, date heure fin>>
les absences
List<Objet<date heure début, date heure fin>>
sorties astreintes
List<Objet<date heure début, date heure fin>>
demandes evp
type(panier repas, transport) et date et statut
id technicien
 
================

sauvegarde en base + statut + calcul total semaine
vérification de non-chevauchement des plages horaires des différentes listes
temps travailler : addition de temps sans chevauchement de base et sortie astreinte avec soustraction des absences
une sortie d'astreinte ne peut être en même temps qu'une absence => si incohérence tout est rejeté
statut semaine en attente

nouvelle réception pour un technicien déjà reçu
vérification sauvegarde du nouveau avec mêmes règles
écrase ce qu'on avait
si statut précédent "en attente" alors nouveau statut "en attente"
si statut précédent "approuvé" alors nouveau statut "à controller"


___


controller

- reçoit les données
  service
- vérifie règle métier et appel ou pas la DAO
  dao
- sauvegarde en base

RequestToto(
planningDeBase List<Plage horaires>,
absences List<Plage horaires>,
sortiesAstreinte List<Plage horaires>,
evp list<evp>,
id technicien,
id semaine
)
Plage horaires:
date heure début
date heure fin

evp:
type,
date
statut

statut semaine

total semaine:
id semaine
nb dheure

____

// type ( PLANNING_BASE, ABSENCE, SORTIE_ASTREINTE)

===============

validiation manager
récupération de datas de la base, validiation ou modification

possibilité de modification de demandes evp ou sortie astreinte
