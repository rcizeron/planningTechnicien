package fr.arolla;

import fr.arolla.domain.PlageHoraire;

import java.util.List;

public record PlanningDTO(
        int idSemaine,
        int idTechnicien,
        List<PlageHoraire> planningDeBase,
        List<PlageHoraire> absences,
        List<PlageHoraire> sortiesAstreintes,
        List<Evp> evps,
        double nbHeuresTravaillees,
        String statut) {
}
