package fr.arolla;

public class PlanningController {

    private final PlanningService planningService = new PlanningService();

    // POST /api/plannings
    public PlanningResponse createPlanning(PlanningRequest planningRequest) {
        try {
            planningService.collecter(planningRequest);
            return new PlanningResponse(201, "Planning créé avec succès.");
        } catch (IllegalArgumentException e) {
            return new PlanningResponse(400, "Erreur: " + e.getMessage());
        }
    }
}
