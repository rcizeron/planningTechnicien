package fr.arolla.domain.entities;

import java.util.List;

public record SuiviDeTemps(
        int idSemaine,
        int idTechnicien,
        PlanningDeBase planningDeBase,
        Absences absences,
        SortiesAstreintes sortiesAstreintes,
        List<Evp> evps,
        double nbHeuresTravaillees,
        String statut) {
}
