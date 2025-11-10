package fr.arolla.api;

import fr.arolla.domain.SuiviDeTempsService;

public class PlanningController {

    private final SuiviDeTempsService suiviDeTempsService = new SuiviDeTempsService();

    // POST /api/plannings
    public PlanningResponse createPlanning(PlanningRequest planningRequest) {
        try {
            suiviDeTempsService.collecter(planningRequest.toDomain());
            return new PlanningResponse(201, "Planning créé avec succès.");
        } catch (IllegalArgumentException e) {
            return new PlanningResponse(400, "Erreur: " + e.getMessage());
        }
    }
}
