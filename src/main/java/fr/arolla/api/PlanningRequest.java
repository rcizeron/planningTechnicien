package fr.arolla.api;

import fr.arolla.domain.entities.Absences;
import fr.arolla.domain.entities.Evp;
import fr.arolla.domain.entities.PlanningDeBase;
import fr.arolla.domain.entities.SortiesAstreintes;
import fr.arolla.domain.entities.SuiviDeTempsRequest;

import java.util.List;

public record PlanningRequest(
        int idSemaine,
        int idTechnicien,
        PlanningDeBase planningDeBase,
        Absences absences,
        SortiesAstreintes sortiesAstreintes,
        List<Evp> evps
) {

    SuiviDeTempsRequest toDomain() {
        return new SuiviDeTempsRequest(
                idSemaine,
                idTechnicien,
                planningDeBase,
                absences,
                sortiesAstreintes,
                evps
        );
    }

}
