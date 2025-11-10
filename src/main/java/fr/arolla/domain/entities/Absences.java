package fr.arolla.domain.entities;

import java.util.List;

public record Absences(
        List<PlageHoraire> plagesHoraire
) implements PlagesHoraire {
    @Override
    public List<PlageHoraire> getPlagesHoraire() {
        return plagesHoraire;
    }

    @Override
    public String getName() {
        return "absences";
    }
}
