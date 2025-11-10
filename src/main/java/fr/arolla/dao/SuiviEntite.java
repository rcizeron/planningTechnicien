package fr.arolla.dao;

import fr.arolla.domain.entities.Evp;
import fr.arolla.domain.entities.PlageHoraire;

import java.util.List;

public record SuiviEntite(
        int idSemaine,
        int idTechnicien,
        List<PlageHoraire> planningDeBase,
        List<PlageHoraire> absences,
        List<PlageHoraire> sortiesAstreintes,
        List<Evp> evps,
        double nbHeuresTravaillees,
        String statut) {
}
