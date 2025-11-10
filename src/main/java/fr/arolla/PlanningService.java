package fr.arolla;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlanningService {

    private final PlanningSQLRepository planningRepository;

    public PlanningService(PlanningSQLRepository repository) {
        this.planningRepository = repository;
    }

    public PlanningService() {
        this.planningRepository = new PlanningSQLRepository();
    }

    public void save(PlanningRequest request) {

        if (request == null) {
            throw new InvalidParameterException("Le planning ne peut pas être null");
        }

        List<String> errors = new ArrayList<>();

        if (request.idSemaine() <= 0) {
            errors.add("idSemaine doit être supérieur à 0");
        }
        if (request.idTechnicien() <= 0) {
            errors.add("idTechnicien doit être supérieur à 0");
        }

        validatePlages("planningDeBase", request.planningDeBase(), errors);
        validatePlages("absences", request.absences(), errors);
        validatePlages("sortiesAstreintes", request.sortiesAstreintes(), errors);

        if (request.evps() != null) {
            for (int i = 0; i < request.evps().size(); i++) {
                Evp evp = request.evps().get(i);
                if (evp == null) {
                    errors.add("evps[" + i + "] est null");
                    continue;
                }
                if (evp.type() == null || evp.type().isBlank()) {
                    errors.add("evps[" + i + "].type est vide");
                }
                if (evp.date() == null) {
                    errors.add("evps[" + i + "].date est null");
                }
            }
        }

        // cross-list overlap checks: ensure no overlap between planningDeBase and astreintes
        checkCrossOverlaps("planningDeBase",
                           request.planningDeBase(),
                           "sortiesAstreintes",
                           request.sortiesAstreintes(),
                           errors);

        // cross-list overlap checks: ensure no overlap between absences and astreintes
        checkCrossOverlaps("absences",
                           request.absences(),
                           "sortiesAstreintes",
                           request.sortiesAstreintes(),
                           errors);

        if (!errors.isEmpty()) {
            throw new InvalidParameterException("Validation errors: " + String.join("; ", errors));
        }

        // calcul du total travailé dans la semaine (en heures)
        long minutes = 0;
        minutes += minutesForList(request.planningDeBase());
        minutes += minutesForList(request.sortiesAstreintes());
        minutes -= minutesForList(request.absences());
        double totalHeures = Math.max(0, minutes / 60.0);
        totalHeures = Math.round(totalHeures * 100.0) / 100.0;

        Optional<PlanningDTO> existing = planningRepository.findById(request.idSemaine(), request.idTechnicien());

        String statut = existing.isEmpty() ||
                        Objects.equals(existing.get().statut(), "EN_ATTENTE") ? "EN_ATTENTE" : "A_CONFIRMER";

        PlanningDTO dto = new PlanningDTO(
                request.idSemaine(),
                request.idTechnicien(),
                request.planningDeBase(),
                request.absences(),
                request.sortiesAstreintes(),
                request.evps(),
                totalHeures,
                statut
        );

        planningRepository.save(dto);
    }

    private void validatePlages(String name, List<PlageHoraire> plages, List<String> errors) {
        if (plages == null) return;

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
    }

    private void checkCrossOverlaps(String nameA,
                                    List<PlageHoraire> a,
                                    String nameB,
                                    List<PlageHoraire> b,
                                    List<String> errors) {
        if (a == null || b == null) return;

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
    }

    private boolean overlaps(PlageHoraire a, PlageHoraire b) {
        return !a.dateFin().isBefore(b.dateDebut()) && !b.dateFin().isBefore(a.dateDebut());
    }

    private long minutesForList(List<PlageHoraire> plages) {
        long minutes = 0;
        for (PlageHoraire p : plages) {
            minutes += java.time.Duration.between(p.dateDebut(), p.dateFin()).toMinutes();
        }
        return minutes;
    }
}

