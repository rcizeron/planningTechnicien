package fr.arolla.dao;

import fr.arolla.domain.SuiviDeTempsRepository;
import fr.arolla.domain.entities.SuiviDeTemps;

import java.util.Optional;

public class PlanningSQLRepository implements SuiviDeTempsRepository {

    @Override
    public void save(SuiviDeTemps planningDTO) {
        // Sauvegarde en base de données (simulée)
    }

    @Override
    public Optional<SuiviDeTemps> findById(int idSemaine, int idTechnicien) {
        // Récupération depuis la base de données (simulée)
        return Optional.empty();
    }
}
