package fr.arolla.domain.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface PlagesHoraire {

    List<PlageHoraire> getPlagesHoraire();

    String getName();

    default List<String> verifierPlage() {
        var plages = getPlagesHoraire();

        List<String> errors = new ArrayList<>();

        if (plages == null) return errors;

        errors.addAll(plages.stream()
                              .flatMap(p -> p.verifierValidite().stream()
                                      .map(s -> getName() + "[" + p + "] : " + s))
                              .toList());

        // check overlaps within same list
        List<PlageHoraire> plagesValides = plages.stream()
                .filter(p -> p.verifierValidite().isEmpty())
                .sorted(Comparator.comparing(PlageHoraire::dateDebut))
                .toList();

        for (int i = 1; i < plagesValides.size(); i++) {
            PlageHoraire prev = plagesValides.get(i - 1);
            PlageHoraire curr = plagesValides.get(i);
            if (!prev.dateFin().isBefore(curr.dateDebut())) {
                errors.add(getName() + " : chevauchement entre " + prev + " et " + curr);
            }
        }
        return errors;
    }

    default List<String> verifierChevauchementCroise(PlagesHoraire other) {

        // TODO à améliorer

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
