package fr.arolla.domain;

import fr.arolla.domain.entities.Evp;
import fr.arolla.domain.entities.PlageHoraire;
import fr.arolla.domain.entities.SuiviDeTemps;
import fr.arolla.domain.entities.SuiviDeTempsRequest;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SuiviDeTempsService {

    private SuiviDeTempsRepository suiviDeTempsRepository;

    public SuiviDeTempsService(SuiviDeTempsRepository repository) {
        this.suiviDeTempsRepository = repository;
    }

    public SuiviDeTempsService() {
    }

    public void collecter(SuiviDeTempsRequest request) {

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

        errors.addAll(request.planningDeBase().verifierPlage());
        errors.addAll(request.absences().verifierPlage());
        errors.addAll(request.sortiesAstreintes().verifierPlage());


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
        errors.addAll(request.planningDeBase().verifierChevauchementCroise(request.sortiesAstreintes()));
        // cross-list overlap checks: ensure no overlap between absences and astreintes
        errors.addAll(request.absences().verifierChevauchementCroise(request.sortiesAstreintes()));

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

        Optional<SuiviDeTemps> existing = suiviDeTempsRepository.findById(request.idSemaine(), request.idTechnicien());

        String statut = existing.isEmpty() ||
                        Objects.equals(existing.get().statut(), "EN_ATTENTE") ? "EN_ATTENTE" : "A_CONFIRMER";

        SuiviDeTemps suiviDeTempsASauvegarder = new SuiviDeTemps(
                request.idSemaine(),
                request.idTechnicien(),
                request.planningDeBase(),
                request.absences(),
                request.sortiesAstreintes(),
                request.evps(),
                totalHeures,
                statut
        );

        suiviDeTempsRepository.save(suiviDeTempsASauvegarder);
    }

    private long minutesForList(List<PlageHoraire> plages) {
        long minutes = 0;
        for (PlageHoraire p : plages) {
            minutes += java.time.Duration.between(p.dateDebut(), p.dateFin()).toMinutes();
        }
        return minutes;
    }
}

