package fr.arolla.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface PlagesHoraire {

    List<PlageHoraire> getPlagesHoraire();

    String getName();

    default List<String> verifierPlage() {
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

    default List<String> verifierChevauchementCroise(PlagesHoraire other) {

        String nameA = this.getName();
        String nameB = other.getName();
        List<PlageHoraire> a = this.getPlagesHoraire();
        List<PlageHoraire> b = other.getPlagesHoraire();

        if (a == null || b == null) return List.of();

        List<String> errors = new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {
            PlageHoraire pa = a.get(i);
            if (pa == null || pa.dateDebut() == null || pa.dateFin() == null) continue;
            for (int j = 0; j < b.size(); j++) {
                PlageHoraire pb = b.get(j);
                if (pb == null || pb.dateDebut() == null || pb.dateFin() == null) continue;
                if (overlaps(pa, pb)) {
                    errors.add("Chevauchement entre " + nameA + "[" + i + "] et " + nameB + "[" + j + "]");
                }
            }
        }
        return errors;
    }

    private boolean overlaps(PlageHoraire a, PlageHoraire b) {
        return !a.dateFin().isBefore(b.dateDebut()) && !b.dateFin().isBefore(a.dateDebut());
    }
}
