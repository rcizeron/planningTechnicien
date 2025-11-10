package fr.arolla.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PlageHoraire(LocalDateTime dateDebut, LocalDateTime dateFin) {

    public List<String> verifierValidite() {

        List<String> errors = new ArrayList<>();

        if (dateDebut == null) {
            errors.add("dateDebut est null");
        }
        if (dateFin == null) {
            errors.add("dateFin est null");
        }
        if (dateDebut != null && dateFin != null && !dateDebut.isBefore(dateFin)) {
            errors.add("dateDebut doit être avant dateFin");
        }

        return errors;
    }
}
