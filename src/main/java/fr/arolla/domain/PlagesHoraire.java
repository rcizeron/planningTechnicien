package fr.arolla.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface PlagesHoraire {

    List<PlageHoraire> getPlagesHoraire();

    String getName();

    default List<String> validerPlage() {
        var plages = getPlagesHoraire();
        var name = getName();

        List<String> errors = new ArrayList<>();

        if (plages == null) return errors;

        for (int i = 0; i < plages.size(); i++) {
            PlageHoraire p = plages.get(i);
            if (p == null) {
                errors.add(name + "[" + i + "] est null");
                continue;
            }
            LocalDateTime debut = p.dateDebut();
            LocalDateTime fin = p.dateFin();
            if (debut == null) {
                errors.add(name + "[" + i + "].dateDebut est null");
            }
            if (fin == null) {
                errors.add(name + "[" + i + "].dateFin est null");
            }
            if (debut != null && fin != null && !debut.isBefore(fin)) {
                errors.add(name + "[" + i + "] : dateDebut doit être avant dateFin");
            }
        }

        // check overlaps within same list
        List<PlageHoraire> copy = new ArrayList<>();
        for (PlageHoraire p : plages) {
            if (p != null && p.dateDebut() != null && p.dateFin() != null) copy.add(p);
        }
        copy.sort(Comparator.comparing(PlageHoraire::dateDebut));
        for (int i = 1; i < copy.size(); i++) {
            PlageHoraire prev = copy.get(i - 1);
            PlageHoraire curr = copy.get(i);
            if (!prev.dateFin().isBefore(curr.dateDebut())) {
                errors.add(name + " : chevauchement entre " + prev + " et " + curr);
            }
        }
        return errors;
    }

}
