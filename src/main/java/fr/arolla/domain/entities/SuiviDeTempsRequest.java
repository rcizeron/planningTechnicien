package fr.arolla.domain.entities;

import java.util.List;

public record SuiviDeTempsRequest(
        int idSemaine,
        int idTechnicien,
        PlanningDeBase planningDeBase,
        Absences absences,
        SortiesAstreintes sortiesAstreintes,
        List<Evp> evps
) {
}
