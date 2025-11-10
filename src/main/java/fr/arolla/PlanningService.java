package fr.arolla;

import fr.arolla.domain.PlageHoraire;

import java.security.InvalidParameterException;
import java.util.ArrayList;
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

    public void collecter(PlanningRequest request) {

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

        if (request.planningDeBase() == null) {
            errors.add("planningDeBase ne peut pas être null");
        }
        if (request.absences() == null) {
            errors.add("absences ne peut pas être null");
        }
        if (request.sortiesAstreintes() == null) {
            errors.add("sortiesAstreintes ne peut pas être null");
        }

        if (!errors.isEmpty()) {
            throw new InvalidParameterException("Validation errors: " + String.join("; ", errors));
        }

        errors.addAll(request.planningDeBase().validerPlage());
        errors.addAll(request.absences().validerPlage());
        errors.addAll(request.sortiesAstreintes().validerPlage());


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
                           request.planningDeBase().plagesHoraire(),
                           "sortiesAstreintes",
                           request.sortiesAstreintes().plagesHoraire(),
                           errors);

        // cross-list overlap checks: ensure no overlap between absences and astreintes
        checkCrossOverlaps("absences",
                           request.absences().plagesHoraire(),
                           "sortiesAstreintes",
                           request.sortiesAstreintes().plagesHoraire(),
                           errors);

        if (!errors.isEmpty()) {
            throw new InvalidParameterException("Validation errors: " + String.join("; ", errors));
        }

        // calcul du total travailé dans la semaine (en heures)
        long minutes = 0;
        minutes += minutesForList(request.planningDeBase().plagesHoraire());
        minutes += minutesForList(request.sortiesAstreintes().plagesHoraire());
        minutes -= minutesForList(request.absences().plagesHoraire());
        double totalHeures = Math.max(0, minutes / 60.0);
        totalHeures = Math.round(totalHeures * 100.0) / 100.0;

        Optional<PlanningDTO> existing = planningRepository.findById(request.idSemaine(), request.idTechnicien());

        String statut = existing.isEmpty() ||
                        Objects.equals(existing.get().statut(), "EN_ATTENTE") ? "EN_ATTENTE" : "A_CONFIRMER";

        PlanningDTO dto = new PlanningDTO(
                request.idSemaine(),
                request.idTechnicien(),
                request.planningDeBase().plagesHoraire(),
                request.absences().plagesHoraire(),
                request.sortiesAstreintes().plagesHoraire(),
                request.evps(),
                totalHeures,
                statut
        );

        planningRepository.save(dto);
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

