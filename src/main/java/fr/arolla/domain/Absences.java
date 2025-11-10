package fr.arolla.domain;

import java.util.List;

public record Absences(
        List<PlageHoraire> plagesHoraire
) implements PlagesHoraireValidation {
    @Override
    public List<PlageHoraire> getPlagesHoraire() {
        return plagesHoraire;
    }

    @Override
    public String getName() {
        return "absences";
    }
}
