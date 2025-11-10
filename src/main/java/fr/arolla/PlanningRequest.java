package fr.arolla;

import fr.arolla.domain.Absences;
import fr.arolla.domain.PlanningDeBase;
import fr.arolla.domain.SortiesAstreintes;

import java.util.List;

public record PlanningRequest(
        int idSemaine,
        int idTechnicien,
        PlanningDeBase planningDeBase,
        Absences absences,
        SortiesAstreintes sortiesAstreintes,
        List<Evp> evps
) {
}
