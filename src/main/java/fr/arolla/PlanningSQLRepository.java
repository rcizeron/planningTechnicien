package fr.arolla;

import java.util.Optional;

public class PlanningSQLRepository {

    public void save(PlanningDTO planningDTO) {
        // Sauvegarde en base de données (simulée)
    }

    public Optional<PlanningDTO> findById(int idSemaine, int idTechnicien) {
        // Récupération depuis la base de données (simulée)
        return Optional.empty();
    }
}
