package fr.arolla;

import java.util.List;

public record PlanningRequest(
        int idSemaine,
        int idTechnicien,
        List<PlageHoraire> planningDeBase,
        List<PlageHoraire> absences,
        List<PlageHoraire> sortiesAstreintes,
        List<Evp> evps
) {
}
