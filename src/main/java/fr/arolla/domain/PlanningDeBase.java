package fr.arolla.domain;

import java.util.List;

public record PlanningDeBase(
        List<PlageHoraire> plagesHoraire
) implements PlagesHoraireValidation {
    @Override
    public List<PlageHoraire> getPlagesHoraire() {
        return plagesHoraire;
    }

    @Override
    public String getName() {
        return "planningDeBase";
    }
}
